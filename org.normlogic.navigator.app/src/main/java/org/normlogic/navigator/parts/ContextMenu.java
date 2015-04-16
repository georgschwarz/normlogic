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

import java.util.Set;
import java.util.TreeSet;

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
import org.normlogic.navigator.core.IHierarchy;
import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.INeighborhoodViewer;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IProperty;
import org.normlogic.navigator.core.IPursuedConclusion;
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
    private IPursuedConclusion pursuedConclusion;
    
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
			IHierarchy<IConcept> conceptHierarchy = currentWorld.getDomains();
			for (IConcept concept : conceptHierarchy.getTopLevelEntities()) {
				createConceptMenu(menu, concept, conceptHierarchy);
			}
		}
	}
	
	private void createConceptMenu(IMenuManager menu, IConcept concept, IHierarchy<IConcept> conceptHierachy) {
        Set<INorm> norms = concept.getObligationNorms(currentWorld);
    	Set<IConcept> subConcepts = conceptHierachy.getDirectChildEntitesOf(concept);
        if (!norms.isEmpty() || !subConcepts.isEmpty()) {
        	MenuManager addIndividualMenu = new MenuManager(concept.getLabel());
        	menu.add(addIndividualMenu);
        	AddIndividualAction action = new AddIndividualAction(concept); 
        	addIndividualMenu.add(action);
        	if (norms.isEmpty()) {
        		addIndividualMenu.setImageDescriptor(IconPool.tripleInContext);
        	}
        	else {
        		addIndividualMenu.setImageDescriptor(IconPool.tripleInConclusion);
        	}
        	if (!subConcepts.isEmpty()) {
        		addIndividualMenu.add(new Separator());
        		for (IConcept subConcept : subConcepts) {
        			createConceptMenu(addIndividualMenu, subConcept, conceptHierachy);
        		}
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
					 InputDialog dialog = new InputDialog(graphViewer.getControl().getShell(), "Hinzufügen", "Name für " + concept.getLabel(),initialValue, null); //$NON-NLS-1$ //$NON-NLS-2$
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
	
	
	class SpecializeIndividualAction extends Action {
		private final IConcept concept;
		private final IIndividual individual;
		public SpecializeIndividualAction(final IIndividual individual, final IConcept concept) {
			super(Messages.ContextMenu_Specialize);
			this.concept = concept;
			this.individual = individual;
			if (individual.hasNotType(concept)) {
				setEnabled(false);
			}
		}
		@Override
	    public void run() {
			individual.assertType(concept);
		}
	}
	
	class RemoveSpecializeIndividualAction extends Action {
		private final IConcept concept;
		private final IIndividual individual;
		public RemoveSpecializeIndividualAction(final IIndividual individual, final IConcept concept) {
			super(Messages.ContextMenu_RemoveSpecialize);
			this.concept = concept;
			this.individual = individual;
		}
		@Override
	    public void run() {
			individual.removeType(concept);
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
                    
                    InputDialog dialog = new InputDialog(graphViewer.getControl().getShell(), "Hinzufügen", "Name für " + concept.getLabel(),initialValue, null); //$NON-NLS-1$ //$NON-NLS-2$
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
            node.unhighlight();
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
           	setImageDescriptor(new NormDecorator(norm).createImage(pursuedConclusion));
        }
        @Override
        public void run() {
        	broker.send(Event.SHOW_NORM, norm);
        }
        
    }
    
    class PursueAction extends Action {
    	IPursuedConclusion pursuedNorms;
        public PursueAction(final IIndividual individual, final IProperty property, final IConcept concept, final Set<INorm> norms) {
            super(Messages.ContextMenu_Pursue);
            pursuedNorms = individual.pursue(property, concept, norms);
        }
        @Override
        public void run() {
        	ContextMenu.this.pursuedConclusion = pursuedNorms;
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
    	
    	ConceptMenu(final IIndividual individual, final IProperty property, final IConcept concept, final IHierarchy<IConcept> conceptHierarchy) {
    		
    		super(concept.getLabel());
            
    		boolean isAssertable = individual.isAssertableWithTriple(property, concept);
            AddAssertionAction action = new AddAssertionAction(individual, property, concept, isAssertable);
            add(action);
            add(new Separator());
            
            Set<INorm> contextNorms = individual.getContextNorms(property, concept);
            if (!contextNorms.isEmpty()) {
            	add(new ContextNormMenu(contextNorms));
            	state = state | INCONTEXT;
            }
            
            Set<INorm> obligationNorms = individual.getObligationNorms(property, concept);
            if (!obligationNorms.isEmpty()) {
	            ObligationNormMenu obligationMenu = new ObligationNormMenu(individual, property, concept, obligationNorms);
	            add(obligationMenu);
	            state = state | INCONCLUSION | obligationMenu.state;
            }
            
            if (!obligationNorms.isEmpty()) {
            	add(new Separator());
                add(new PursueAction(individual, property, concept, obligationNorms));
            }
            
            if (pursuedConclusion != null && isAssertable) {
            	isPursued = pursuedConclusion.dependsOn(individual, property, concept);
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
    	
    	PropertyMenu(final IIndividual individual, final IProperty property, final IHierarchy<IConcept> conceptHierarchy) {
    		
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

    class SpecializeMenu extends MenuManager {
    	
    	int state = NONE;
    	
    	SpecializeMenu(final IIndividual individual, final IConcept concept) {
    		
    		super(concept.getLabel());
    		
        	if(individual.hasType(concept)) {
        		setImageDescriptor(IconPool.overlayOk);
        		add(new RemoveSpecializeIndividualAction(individual, concept));
        	}
        	else {
        		add(new SpecializeIndividualAction(individual, concept));
               	if (pursuedConclusion != null) {
	               	if (pursuedConclusion.dependsON(individual, concept)) {
	               		state = state | HASTOBESPECIALIZED;
	               	}
               	}
        	}
        	
        	Set<IConcept> subConcepts = currentWorld.retainIncluded(concept.getSubConcepts(true)).getEntites();
        	if (!subConcepts.isEmpty()) {
        		add(new Separator());
            	for (IConcept subConcept : subConcepts) {
            		add(new SpecializeMenu(individual, subConcept));
            	}
        	}
        	add(new Separator());
        	Set<INorm> norms = individual.getContextNorms(concept);
        	add(new ContextNormMenu(norms));
        	
        	if ((state & HASTOBESPECIALIZED)>0) {
        		setImageDescriptor(IconPool.overlayStar);
        	}
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
        // let´s see if we have a world to describe a situation ...
        if (currentWorld == null) {
            return;
        }
        final IIndividual individual = (IIndividual)data;
        
        MenuManager specializeMenu = new MenuManager(Messages.ContextMenu_Specialize);
        menu.add(specializeMenu);
        IHierarchy<IConcept> types = currentWorld.retainIncluded(individual.getTypes().getEntites());
        int state = NONE;
        for (IConcept concept : types.getLeafEntities()) {
        	IHierarchy<IConcept> subConcepts = currentWorld.retainIncluded(concept.getSubConcepts(true));
        	for (IConcept subConcept : subConcepts.getEntites()) {
        		SpecializeMenu subSpecializeMenu = new SpecializeMenu(individual, subConcept); 
        		specializeMenu.add(subSpecializeMenu);
        		state = state | subSpecializeMenu.state;
        	}
        }
        if ((state & HASTOBESPECIALIZED)>0) {
        	specializeMenu.setImageDescriptor(IconPool.overlayStar);
        }

        if (!specializeMenu.isEmpty()) {
        	menu.add(new Separator());
        }
        
        individual.renderNeighborhood(currentWorld, new INeighborhoodViewer() {
            @Override
            public void add(final IProperty property, final IHierarchy<IConcept> conceptHierarchy) {
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
	static int HASTOBESPECIALIZED = 16;
    
   
    @Override
    public AbstractZoomableViewer getZoomableViewer() {
        return graphViewer;
    }
    
    public void setCurrentWorld(final INormedWorld world) {
        currentWorld = world;
    }
}
