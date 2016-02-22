package org.normlogic.navigator.parts;

import java.awt.event.ItemListener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.normlogic.navigator.core.Event;
import org.normlogic.navigator.core.IKnowledgeBase;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.IPursuedConclusion;
import org.normlogic.navigator.util.Messages;
import org.normlogic.navigator.util.NormDecorator;

public class PursuePart implements IStructuredContentProvider, ILabelProvider {
	
	CLabel txtLabel;
	TableViewer tableViewer;
	IPursuedConclusion pursuedConclusion;
	IEventBroker broker;
	
	@Inject
	MPart part;
	
	@Inject
	EPartService partService;
	
	@Inject
	ESelectionService selectionService;
	

	@PostConstruct
	public void createComposite(Composite parent, final IEventBroker broker) {
		
		this.broker = broker;
		
		parent.setLayout(new GridLayout(1, false));

		txtLabel = new CLabel(parent, SWT.BOLD);
		txtLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		tableViewer = new TableViewer(parent);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tableViewer.setLabelProvider(this);
		tableViewer.setContentProvider(this);
		tableViewer.setInput("PursuedNorms");
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
				selectionService.setSelection(selection.getFirstElement());
			}
		});
		
	}
	
	
	private void selectNorm(INorm norm) {
		if (norm != null && pursuedConclusion != null) {
			if (pursuedConclusion.contains(norm)) {
				StructuredSelection selection = new StructuredSelection(norm);
				tableViewer.setSelection(selection);
			}
			else {
				tableViewer.setSelection(StructuredSelection.EMPTY);
			}
		}
	}
	
	@Inject
	public void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) INorm norm) {
		selectNorm(norm);
	}
	
	@Inject
	@Optional
	private void pursueNorm(@UIEventTopic(Event.PURSUE_NORM) IPursuedConclusion pursuedConclusion) {
		
		this.pursuedConclusion = pursuedConclusion;
		if (pursuedConclusion != null) {
			txtLabel.setText(pursuedConclusion.toString());
			tableViewer.refresh();
			partService.bringToTop(part);
		}
	}
	
	@Inject
	@Optional
	private void showNorm(@UIEventTopic(Event.SHOW_NORM) 
	    INorm norm) {
		selectNorm(norm);
	}
	
	@Inject
	@Optional
	private void ontologyChanged(@UIEventTopic(Event.ONTOLOGY_CHANGED) 
	    IKnowledgeBase kb) {
		tableViewer.refresh();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement == "PursuedNorms") {
			if (pursuedConclusion != null) {
				return pursuedConclusion.getNorms().toArray();
			}
		}
		Object[] objects = {};  
		return objects;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
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
			return imagedNorm.createImage(pursuedConclusion).createImage();
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof INorm) {
			return ((INorm)element).getLabel();
		}
		return null;
	} 
}
