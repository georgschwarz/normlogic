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

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;
import org.normlogic.navigator.core.Event;
import org.normlogic.navigator.core.IAssertionTriple;
import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.IKnowledgeBase;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IOntology;
import org.normlogic.navigator.core.IPursuedNorms;
import org.normlogic.navigator.core.ISituationViewer;
import org.normlogic.navigator.core.ModelContainer;
import org.normlogic.navigator.core.impl.NormedWorld;
import org.normlogic.navigator.util.IconPool;
import org.normlogic.navigator.util.NormDecorator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class LibraryPart {
	
	TreeViewer treeViewer;
	ModelContainer container;
	INormedWorld normedWorld;
	IPursuedNorms pursuedNorms;
	
	Set<INorm> selectedNorms = new HashSet<>();
	final Set<IIndividual> individuals = new HashSet<>();
	
	@Inject
	ESelectionService selectionService;
	
	@PostConstruct
	public void createComposite(Composite parent) {
		container = ModelContainer.getContainer();
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(container);
		treeViewer.setLabelProvider(new LibraryLabelProvider());
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
				selectionService.setSelection(selection.getFirstElement());
			}
		});
		treeViewer.setInput("Library");
		treeViewer.expandAll();
	}
	
	@Inject
	@Optional
	private void getNotifiedLoaded(@UIEventTopic(Event.ONTOLOGY_LOADED) 
	    IKnowledgeBase kb) {
		treeViewer.refresh();
		treeViewer.expandAll();
	} 
	
	@Inject
	@Optional
	private void showNorm(@UIEventTopic(Event.SHOW_NORM) 
	    INorm norm) {
		if (norm != null) {
			StructuredSelection selection = new StructuredSelection(norm);
			treeViewer.getTree().setFocus();
			treeViewer.setSelection(selection);
		}
	} 

	@Inject
	@Optional
	private void pursueNorm(@UIEventTopic(Event.PURSUE_NORM) 
	    IPursuedNorms norms) {
		pursuedNorms = norms;
		treeViewer.refresh();
		// showNorm(norm);
	} 
	
	@Inject
	@Optional
	private void ontologyChanged(@UIEventTopic(Event.ONTOLOGY_CHANGED) 
	    IKnowledgeBase kb) {
		kb.visualize(new ISituationViewer() {
			
			@Override
			public void changeNormedWorld(INormedWorld world) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void add(IAssertionTriple triple) {
				individuals.add(triple.getSource());
				individuals.add(triple.getTarget());
			}
			
			@Override
			public void add(IIndividual individual) {
				// TODO Auto-generated method stub
				
			}
		});
	} 
	
	@Inject
	public void setText(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) IIndividual individual) {
		if (individual != null && normedWorld != null) {
			selectedNorms = individual.getContextNorms(normedWorld);
			treeViewer.refresh();
		}
	}
	
	@Inject
	@Optional
	private void normedWorldChanged(@UIEventTopic(Event.NORMED_WORLD_CHANGED) 
	    INormedWorld world) {
		normedWorld = world;
	} 
	
	class LibraryLabelProvider implements ILabelProvider {
		
		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof INorm) {
				NormDecorator imagedNorm = new NormDecorator((INorm)element);
				return imagedNorm.createImage(pursuedNorms).createImage();
			}
			else if (element instanceof IOntology) {
				return IconPool.ontologyDefault.createImage();
			}
			return null;
		}

		@Override
		public String getText(Object element) {

			if (element instanceof IOntology) {
 				return ((IOntology)element).getLabel();
			}
			else if (element instanceof INorm) {
				return ((INorm)element).getLabel();
			}
			return null;
		}
	}

	@Focus
	public void setFocus() {
		
	}

}
