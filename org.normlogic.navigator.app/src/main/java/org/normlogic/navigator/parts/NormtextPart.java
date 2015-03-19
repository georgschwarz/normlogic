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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.normlogic.navigator.core.Event;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.IPursuedNorms;
import org.normlogic.navigator.util.NormDecorator;

public class NormtextPart {

	CLabel txtLabel;
	Text txtViewer;
	IPursuedNorms pursuedNorms;
	INorm norm;

	@Inject
	private MDirtyable dirty;
	
	@Inject
	ESelectionService selectionService;

	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		txtLabel = new CLabel(parent, SWT.BOLD);
		txtLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		txtViewer = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
		txtViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	@Inject
	@Optional
	private void pursueNorm(@UIEventTopic(Event.PURSUE_NORM) 
	    IPursuedNorms norms) {
		pursuedNorms = norms;
		setText(norm);
	} 


	@Inject
	public void setText(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) INorm norm) {
		if (norm != null) {
			this.norm = norm;
			txtLabel.setText(norm.getLabel());
			txtLabel.setImage(new NormDecorator(norm).createImage(pursuedNorms).createImage());
			txtViewer.setText(norm.getText());
		}
	}
	
	@Focus
	public void setFocus() {
		
	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}
}
