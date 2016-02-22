package org.normlogic.navigator.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.normlogic.navigator.core.Event;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.IPursuedConclusion;
import org.normlogic.navigator.util.Messages;
import org.normlogic.navigator.util.NormDecorator;

public class NormRepresentation {
	
	CLabel txtLabel;
	
	Group conditionFrame;
	Group conclusionFrame;
	
	StyledText conditionViewer;
	StyledText conclusionViewer;
	INorm  norm;
	IPursuedConclusion pursuedNorms;
	
	@PostConstruct
	public void createComposite(Composite parent) {

		parent.setLayout(new GridLayout(2, false));

		txtLabel = new CLabel(parent, SWT.BOLD);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		txtLabel.setLayoutData(gridData);
		
		//conditionFrame = new Group(parent, SWT.SHADOW_ETCHED_IN);
		//conditionFrame.setText(Messages.NormRepresentationPart_Condition);
		//conditionFrame.setLayoutData(new GridData(GridData.FILL_BOTH));
		//conditionFrame.setLayout(new GridLayout(1, false));
		conditionViewer = new StyledText(parent, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		conditionViewer.setLayoutData(new GridData(GridData.FILL_BOTH));

		/*
		conclusionFrame = new Group(parent, SWT.NONE);
		conclusionFrame.setText(Messages.NormRepresentationPart_Conclusion);
		conclusionFrame.setLayoutData(new GridData(GridData.FILL_BOTH));
		FillLayout conclusionLayout = new FillLayout();
		conclusionLayout.marginHeight = 5;
		conclusionLayout.marginWidth = 3;
		conclusionFrame.setLayout(conclusionLayout);
		*/
		conclusionViewer  = new StyledText(parent, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		conclusionViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		if (norm != null) {
			setText(norm);
		}
	}
	
	@Inject
	@Optional
	private void pursueNorm(@UIEventTopic(Event.PURSUE_NORM) 
	    IPursuedConclusion norms) {
		pursuedNorms = norms;
		setText(norm);
	} 
	
	private void selectNorm(INorm norm) {
		if (norm != null) {
			this.norm = norm;
			if (txtLabel != null) {
				txtLabel.setText(norm.getLabel());
				txtLabel.setImage(new NormDecorator(norm).createImage(pursuedNorms).createImage());
				
				conditionViewer.setText(norm.getRepresentationCondition());
				conclusionViewer.setText(norm.getRepresentationConclusion());
			}
		}
	}
	
	@Inject
	@Optional
	private void showNorm(@UIEventTopic(Event.SHOW_NORM) 
	    INorm norm) {
		selectNorm(norm);
	} 
	
	@Inject
	public void setText(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) INorm norm) {
		selectNorm(norm);
	}
}
