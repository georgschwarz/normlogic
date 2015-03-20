/*******************************************************************************
 * Copyright (c) 2015 Georg Schwarz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     Georg Schwarz - initial API and implementation
 ******************************************************************************/
package org.normlogic.navigator.parts;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.normlogic.navigator.core.Event;
import org.normlogic.navigator.core.IAddEntityDialog;
import org.normlogic.navigator.core.IConcept;
import org.normlogic.navigator.core.IHierarchyMaker;
import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.INeighborhoodViewer;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IProperty;
import org.normlogic.navigator.core.IPursuedNorms;
import org.normlogic.navigator.core.ModelContainer;
import org.normlogic.navigator.core.impl.Individual;
import org.normlogic.navigator.core.util.Tracker;
import org.normlogic.navigator.util.IconPool;
import org.normlogic.navigator.util.Messages;
import org.normlogic.navigator.util.NormDecorator;
import org.normlogic.navigator.util.NormImage;

/**
 * Creates the context menu for the selected graph node.
 */
class ContextMenu implements IMenuListener, IZoomableWorkbenchPart {
    
    private final GraphViewer graphViewer;
    
    private INormedWorld currentWorld;
    private IEventBroker broker;
    private IPursuedNorms pursuedNorms;
    
    /**
     * Creates a new instance of {@link ContextMenu}.
     * @param currentWorld2
     *
     * @param ontology
     *            the ontology
     * @param graphViewer
     *            the graph viewer to create the context menu for
     * @param broker 
     */
    ContextMenu(final GraphViewer graphViewer, IEventBroker broker) {
        this.graphViewer = graphViewer;
        this.broker = broker;
    }
    
    /**
     * Add the items to the context menu. Checks that there is a single
     * selection and that the selected element is a {@link GraphNode} that
     * contains a {@link Individual} as data property.
     *
     * @param menu
     *            the menu manager
     */
    @Override
    public void menuAboutToShow(final IMenuManager menu) {
        Graph graphControl = graphViewer.getGraphControl();
        if (graphControl.getSelection().size() == 1) {
            final Object selection = graphControl.getSelection().get(0);
            if (selection instanceof GraphNode) {
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        createNodeMenu(menu, (GraphNode)selection);
                    }
                };
                BusyIndicator.showWhile(graphControl.getDisplay(), task);
            }
        }
        else {
        	Runnable task = new Runnable() {
                @Override
                public void run() {
                    createBlankMenu(menu);
                }

            };
            BusyIndicator.showWhile(graphControl.getDisplay(), task);
      	}
    }
    
	private void createBlankMenu(IMenuManager menu) {
		if (currentWorld != null) {
			IHierarchyMaker<IConcept> conceptHierarchy = currentWorld.getDomains();
			for (IConcept concept : conceptHierarchy.getTopLevelEntities()) {
				createConceptMenu(menu, concept, conceptHierarchy);
			}
		}
	}
	
	private void createConceptMenu(IMenuManager menu, IConcept concept, IHierarchyMaker<IConcept> conceptHierachy) {
		MenuManager addIndividualMenu = new MenuManager(concept.getLabel());
        menu.add(addIndividualMenu);
        AddIndividualAction action = new AddIndividualAction(concept); 
        addIndividualMenu.add(action);
        Set<INorm> norms = concept.getObligationNorms(currentWorld);
        if (norms.isEmpty()) {
        	addIndividualMenu.setImageDescriptor(IconPool.tripleInContext);
        }
        else {
        	addIndividualMenu.setImageDescriptor(IconPool.tripleInConclusion);
        }
		Set<IConcept> subConcepts = conceptHierachy.getDirectChildEntitesOf(concept);
		if (!subConcepts.isEmpty()) {
			addIndividualMenu.add(new Separator());
			for (IConcept subConcept : subConcepts) {
				createConceptMenu(addIndividualMenu, subConcept, conceptHierachy);
			}
		}
	}
	
	class AddIndividualAction extends Action {
		private final IConcept concept;
		public AddIndividualAction(final IConcept concept) {
			super(Messages.ContextMenu_AddIndividual);
			this.concept = concept;
		}
		@Override
	    public void run() {
			concept.createAssertedIndividual(new IAddEntityDialog() {
				@Override
				public String runDialog(String initialValue) {
					 InputDialog dialog = new InputDialog(graphViewer.getControl().getShell(), "Hinzuf�gen", "Name f�r " + concept.getLabel(),initialValue, null); //$NON-NLS-1$ //$NON-NLS-2$
	                    int result = dialog.open();
	                    if (result == Window.OK) {
	                        return dialog.getValue();
	                    }
	                    else {
	                        return null;
	                    }
				}
			});
		}
	}

	class AddAssertionAction extends Action  {
        
        private final IIndividual individual;
        private final IProperty property;
        private final IConcept concept;
        
        /**
         * Creates a new instance of {@link ContextMenu.MyAction}.
         */
        public AddAssertionAction(final IIndividual individual, final IProperty property, final IConcept concept, boolean isEnabled) {
            super(Messages.ContextMenu_AddAssertion);
            this.individual = individual;
            this.property = property;
            this.concept = concept;
            setEnabled(isEnabled);
        }
        @Override
        public void run() {
            individual.addAssertionFromConcept(property, concept, new IAddEntityDialog() {
                @Override
                public String runDialog(final String initialValue) {
                    
                    InputDialog dialog = new InputDialog(graphViewer.getControl().getShell(), "Hinzuf�gen", "Name f�r " + concept.getLabel(),initialValue, null); //$NON-NLS-1$ //$NON-NLS-2$
                    int result = dialog.open();
                    if (result == Window.OK) {
                        return dialog.getValue();
                    }
                    else {
                        return null;
                    }
                }
            });
        }
    }
    
    class RemoveIndividualAction extends Action {
        private final IIndividual individual;
        private final GraphNode node;
        
        public RemoveIndividualAction(final IIndividual individual, final GraphNode node) {
            super(new String(Messages.ContextMenu_DeleteIndividual));
            this.individual = individual;
            this.node = node;
        }
        
        @Override
        public void run() {
            node.dispose();
            individual.deleteFromKnowledgeBase();
        }
    }
    
    class CloseAssertionAction extends Action {
        private final IIndividual individual;
        private final GraphNode node;
        
        public CloseAssertionAction(final IIndividual individual, final GraphNode node) {
            super(new String(Messages.ContextMenu_DeleteAssertion));
            this.individual = individual;
            this.node = node;
        }
        
        @Override
        public void run() {
            node.dispose();
            individual.deleteFromKnowledgeBase();
        }
    }
    
    class ShowNormAction extends Action {
    	INorm norm;
        public ShowNormAction(final INorm norm) {
            super(norm.getLabel());
            this.norm = norm;
           	setImageDescriptor(new NormDecorator(norm).createImage(pursuedNorms));
        }
        @Override
        public void run() {
        	broker.send(Event.SHOW_NORM, norm);
        }
        
    }
    
    class PursueNormAction extends Action {
    	IPursuedNorms pursuedNorms;
        public PursueNormAction(final IIndividual individual, final Set<INorm> norms) {
            super(Messages.ContextMenu_Pursue);
            pursuedNorms = individual.pursue(norms);
        }
        @Override
        public void run() {
        	ContextMenu.this.pursuedNorms = pursuedNorms;
        	ModelContainer.getContainer().setPursuedNorms(pursuedNorms);
        	broker.send(Event.PURSUE_NORM, pursuedNorms);
        }
    }
    
    class NormMenuManager extends MenuManager {
    	NormMenuManager(String label) {
    		super(label);
    	}
    	@Override
    	protected void update(boolean force, boolean recursive) {
    		super.update(force, recursive);
    		Menu menu = getMenu();
    		if (menu != null) {
    			MenuItem[] items = menu.getItems();
    			for (int i = 0; i<items.length; i++) {
    				final MenuItem item = items[i];
    				item.addArmListener(new ArmListener() {
						@Override
						public void widgetArmed(ArmEvent e) {
							Object o = item.getData();
							if (o instanceof ActionContributionItem) {
								ActionContributionItem actionItem = (ActionContributionItem)o;
								IAction action = actionItem.getAction();
								action.run();
								/*
								if (action instanceof ShowNormAction) {
									INorm norm = ((ShowNormAction)action).norm;
									DefaultToolTip toolTip = new DefaultToolTip(graphViewer.getControl());
									String text = norm.getLabel() + "\n\n" + norm.getText();
									toolTip.setText(text);
									Point cursorLocation = Display.getCurrent().getCursorLocation();
									cursorLocation.y = cursorLocation.y + 10;
									toolTip.show(Display.getCurrent().getFocusControl().toControl(cursorLocation));
								}
								*/
							}
						}
					});
    			}
    		}
    	}
    }
    

    class ContextNormMenu extends NormMenuManager {
    	
    	ContextNormMenu(final Set<INorm> norms) {
    		super(Messages.ContextMenu_ConditionFor);
            if (norms.isEmpty()) return;
            for (INorm norm : norms) {
               	add(new ShowNormAction(norm));
            }
            setImageDescriptor(IconPool.normDefault);
    	}
    }
    
    class ObligationNormMenu extends NormMenuManager {

    	int state = NONE;
    	
    	ObligationNormMenu(final IIndividual individual, final IProperty property, final IConcept concept, final Set<INorm> norms) {

    		super(Messages.ContextMenu_ConclusionOf);
    		
    		if  (norms.isEmpty()) return;
    		
            boolean hasFullfilledNorm = false;
            boolean hasNormToBeFullfilled = false;

            for (INorm norm : norms) {
            	if (norm.isFulfilledFor(individual)) {
            		hasFullfilledNorm = true;
            	}
            	else if (norm.hasToBeFulfilledFor(individual)) {
            		hasNormToBeFullfilled = true;
            	}
            	add(new ShowNormAction(norm));
            }
            
            if (hasNormToBeFullfilled) {
            	setImageDescriptor(IconPool.normHasToBeFullfilled);
            	state = state | HASTOBEFULLFILLED;
            }
            else if (hasFullfilledNorm) {
            	setImageDescriptor(IconPool.normIsFullfilled);
            	state = state | FULLFILLED;
            }
            else {
            	setImageDescriptor(IconPool.normDefault);
            }
    	}	
    }
    
    class ConceptMenu extends MenuManager {
    	
    	int state = NONE;
    	boolean isPursued = false;
    	
    	ConceptMenu(final IIndividual individual, final IProperty property, final IConcept concept, final IHierarchyMaker<IConcept> conceptHierarchy) {
    		
    		super(concept.getLabel());
            
    		boolean isAssertable = individual.isAssertableWithConcept(property, concept);
            AddAssertionAction action = new AddAssertionAction(individual, property, concept, isAssertable);
            add(action);
            add(new Separator());
            
            Set<INorm> contextNorms = individual.getContextNorms(currentWorld, property, concept);
            if (!contextNorms.isEmpty()) {
            	add(new ContextNormMenu(contextNorms));
            	state = state | INCONTEXT;
            }
            
            Set<INorm> obligationNorms = individual.getObligationNorms(currentWorld, property, concept);
            if (!obligationNorms.isEmpty()) {
	            ObligationNormMenu obligationMenu = new ObligationNormMenu(individual, property, concept, obligationNorms);
	            add(obligationMenu);
	            state = state | INCONCLUSION | obligationMenu.state;
            }
            
            if (!obligationNorms.isEmpty()) {
            	add(new Separator());
                add(new PursueNormAction(individual, obligationNorms));
            }
            
            if (pursuedNorms != null && isAssertable) {
            	if (pursuedNorms.relevantFor(currentWorld, individual, property, concept)) {
            		if (!individual.hasAssertion(property, concept)) {
            			if (individual.hasPursuedTriple(pursuedNorms, property, concept)) {
            				isPursued =  true;
            			}
            		}
            	}
            }

            for (IConcept subConcept : conceptHierarchy.getDirectChildEntitesOf(concept)) {
            	ConceptMenu subConceptMenu = new ConceptMenu(individual, property, subConcept, conceptHierarchy);
            	add(subConceptMenu);
                state = state | subConceptMenu.state;
            }

            setImageDescriptor(findImageFor(state, isPursued));
            action.setImageDescriptor(findImageFor(state, isPursued));
    	}
    }
    
    class PropertyMenu extends MenuManager {
    	
    	PropertyMenu(final IIndividual individual, final IProperty property, final IHierarchyMaker<IConcept> conceptHierarchy) {
    		
        	super(property.getLabel());

        	int state = INCONTEXT;
        	boolean isPursued = false;
        	
        	for (IConcept concept : conceptHierarchy.getTopLevelEntities()) {
        		ConceptMenu conceptMenu = new ConceptMenu(individual, property, concept, conceptHierarchy);
                add(conceptMenu);
        		state = state | conceptMenu.state;
        		isPursued = isPursued || conceptMenu.isPursued;
        	}
        	
    		setImageDescriptor(findImageFor(state, isPursued));
    	}
    }

    ImageDescriptor findImageFor(int state, boolean marked) {
    	ImageDescriptor image;
    	if ((state & INCONCLUSION) > 0) {
    		image = IconPool.tripleInConclusion;
    	}
    	else {
    		image = IconPool.tripleInContext;
    	}

    	if ((state & FULLFILLED) > 0) image = new DecorationOverlayIcon(image.createImage(), IconPool.overlayOk, IDecoration.BOTTOM_RIGHT);
        if ((state & HASTOBEFULLFILLED) > 0)  image = new DecorationOverlayIcon(image.createImage(), IconPool.overlayCaution, IDecoration.BOTTOM_RIGHT); 
        
        if (marked) {
        	return new DecorationOverlayIcon(image.createImage(), IconPool.overlayStar, IDecoration.TOP_LEFT);
        }
        return image;
    }

    private void createNodeMenu(final IMenuManager menu, final GraphNode node) {
        Object data = node.getData();
        if (!(data instanceof IIndividual)) {
            return;
        }
        // let�s see if we have a world to describe a situation ...
        if (currentWorld == null) {
            return;
        }
        final IIndividual individual = (IIndividual)data;
        individual.renderNeighborhood(currentWorld, new INeighborhoodViewer() {
            @Override
            public void add(final IProperty property, final IHierarchyMaker<IConcept> conceptHierarchy) {
                menu.add(new PropertyMenu(individual, property, conceptHierarchy));
            }
        });
        menu.add(new Separator());
        menu.add(new RemoveIndividualAction(individual, node));
    }
    
    static int NONE = 0;
	static int INCONTEXT = 1;
	static int INCONCLUSION = 2;
	static int HASTOBEFULLFILLED = 4;
	static int FULLFILLED = 8;
    
   
    @Override
    public AbstractZoomableViewer getZoomableViewer() {
        return graphViewer;
    }
    
    public void setCurrentWorld(final INormedWorld world) {
        currentWorld = world;
    }
}
