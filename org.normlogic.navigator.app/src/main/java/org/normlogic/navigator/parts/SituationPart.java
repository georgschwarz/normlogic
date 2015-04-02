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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.draw2d.Label;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.internal.ZoomManager;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.normlogic.navigator.core.Event;
import org.normlogic.navigator.core.IAssertionTriple;
import org.normlogic.navigator.core.IConcept;
import org.normlogic.navigator.core.IHierarchy;
import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.IKnowledgeBase;
import org.normlogic.navigator.core.INeighborhoodViewer;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IProperty;
import org.normlogic.navigator.core.IPursuedConclusion;
import org.normlogic.navigator.core.ISituationViewer;
import org.normlogic.navigator.core.impl.NormedWorld;
import org.normlogic.navigator.parts.ContextMenu;
import org.normlogic.navigator.util.IconPool;

public class SituationPart implements ISituationViewer {
    
	private GraphViewer graph;
    private ContextMenu contextMenu;
    private ZoomManager zoom;
    INormedWorld normedWorld;
    private IEventBroker broker;
    IPursuedConclusion pursuedConclusion;
    IKnowledgeBase kb;
	
    @Inject
	ESelectionService selectionService;
    
	@PostConstruct
	public void createComposite(Composite parent, IEclipseContext context, IEventBroker broker) {
		this.broker = broker;
		graph = new GraphViewer(parent, SWT.BORDER);
		parent.setData(graph);
        graph.setConnectionStyle(ZestStyles.CONNECTIONS_SOLID);
        graph.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)graph.getSelection();
				selectionService.setSelection(selection.getFirstElement());
			}
		});
        attachMenu();        
        graph.getGraphControl().applyLayout();
        zoom = new ZoomManager(graph.getGraphControl().getRootLayer(), graph.getGraphControl().getViewport());
        context.set(GraphViewer.class, graph);
        context.set(ZoomManager.class, zoom);
	}
	
	@Inject
	@Optional
	private void ontologyLoaded(@UIEventTopic(Event.ONTOLOGY_LOADED) 
	    IKnowledgeBase kb) {
		this.kb = kb;
		kb.updateIndividualTypes();
		kb.visualize(this);
	} 

	@Inject
	@Optional
	private void ontologyChanged(@UIEventTopic(Event.ONTOLOGY_CHANGED) 
	    IKnowledgeBase kb) {
		this.kb = kb;
		kb.updateIndividualTypes();
		kb.visualize(this);
	} 

	@Inject
	@Optional
	private void normedWorldChanged(@UIEventTopic(Event.NORMED_WORLD_CHANGED) 
	    NormedWorld world) {
		normedWorld = world;
		if (contextMenu != null)
			contextMenu.setCurrentWorld(world);
	} 
	
	@Inject
	@Optional
	private void pursueNorm(@UIEventTopic(Event.PURSUE_NORM) 
	    IPursuedConclusion norms) {
		pursuedConclusion = norms;
		if (kb != null) {
			kb.visualize(this);
		}
	} 

	GraphNode addGraphNode(final IIndividual individual) {
        GraphNode graphNode = null;
		@SuppressWarnings("rawtypes")
		List nodes = graph.getGraphControl().getNodes();
        for (Object object :  nodes) {
            if (object instanceof GraphNode) {
                if (((GraphNode)object).getData().equals(individual)) {
                    graphNode = (GraphNode)object;
                }
            }
        }
        if (graphNode == null) {
        	graphNode = new GraphNode(graph.getGraphControl(), SWT.NONE, individual.getLabel(), individual);
        }
        String tooltip = new String("Types:\n");
        for (IConcept type : normedWorld.retainIncluded(individual.getTypes().getEntites()).getEntites()) {
        	tooltip = tooltip + type.getLabel() + "\n";
        }
        graphNode.setTooltip(new Label(tooltip.trim()));
        Set<INorm> norms = individual.getObligationNorms();
        ImageDescriptor image;
        if (norms.isEmpty()) {
        	image = IconPool.tripleInContext;
        }
        else {
        	image = IconPool.tripleInConclusion;
        }
        boolean hasToBeFullfilled = false;
        boolean isFullfilled = false;
        for (INorm norm : norms) {
        	if (norm.isFulfilledFor(individual)) {
        		isFullfilled = true;
        	}
        	else if (norm.hasToBeFulfilledFor(individual)) {
        		hasToBeFullfilled = true;
        	}
        	if (hasToBeFullfilled && isFullfilled) {
        		break;
        	}
        }
   		if (isFullfilled) {
        	image = new DecorationOverlayIcon(image.createImage(), IconPool.overlayOk, IDecoration.BOTTOM_RIGHT);
        }
   		if (hasToBeFullfilled) {
   			image = new DecorationOverlayIcon(image.createImage(), IconPool.overlayCaution, IDecoration.BOTTOM_RIGHT);
   		}
   		final Map<IProperty, IConcept> pursuedRelations = new HashMap<>();
        if (pursuedConclusion != null) {
	        if (pursuedConclusion.dependsOn(individual)) {
	        	individual.renderNeighborhood(normedWorld, new INeighborhoodViewer() {
					@Override
					public void add(IProperty property, IHierarchy<IConcept> concepts) {
						for (IConcept concept: concepts.getLeafEntities()) {
							if (!individual.hasAssertion(property, concept)) {
								if (individual.hasPursuedTriple(pursuedConclusion, property, concept)) {
									pursuedRelations.put(property, concept);
								}
							}
						}
					}
				});
	        }
        }
        if (!pursuedRelations.isEmpty()) {
     		image = new DecorationOverlayIcon(image.createImage(), IconPool.overlayStar, IDecoration.BOTTOM_RIGHT);
        }
        graphNode.setImage(image.createImage());
        return graphNode;
    }
    
    GraphConnection addGraphConnection(final IAssertionTriple triple) {
        
        GraphNode source = addGraphNode(triple.getSource());
        GraphNode target = addGraphNode(triple.getTarget());
        @SuppressWarnings("rawtypes")
		List connections = graph.getGraphControl().getConnections();
        for (Object object : connections) {
            if (object instanceof GraphConnection) {
                GraphConnection  graphConnection = (GraphConnection)object;
                if (graphConnection.getData().equals(triple)) {
                    return graphConnection;
                }
                
            }
        }
        GraphConnection connection = new GraphConnection(graph.getGraphControl(), SWT.NONE, source, target);
        connection.setTooltip(new Label(triple.getLabel()));
        connection.setData(triple);
        return connection;
    }
    
    /**
     * Creates the context menu for the graph.
     */
    private void attachMenu() {
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.setRemoveAllWhenShown(true);
        contextMenu = new ContextMenu(graph, broker);
        manager.addMenuListener(contextMenu);
        graph.getGraphControl().setMenu(manager.createContextMenu(graph.getGraphControl()));
        // getSite().registerContextMenu(manager, graph);
    }

    @Override
    public void add(final IIndividual individual) {
        addGraphNode(individual);
    }
    
    @Override
    public void add(final IAssertionTriple triple) {
        addGraphConnection(triple);
    }
    
    @Override
    public void changeNormedWorld(final INormedWorld world) {
        if (contextMenu == null) {
            return;
        }
        contextMenu.setCurrentWorld(world);
    }
    
}
