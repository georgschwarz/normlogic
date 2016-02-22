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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.normlogic.navigator.core.Event;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.IPursuedConclusion;
import org.normlogic.navigator.util.NormDecorator;

public class NormtextPart {

	CLabel txtLabel;
	// Text txtViewer;
	Browser browser;
	IPursuedConclusion pursuedNorms;
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

		browser = new Browser(parent, SWT.BORDER);
		// TODO: sudo apt-get install libwebkitgtk-1.0-0 to get it work on ubuntu 15.10 
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		browser.addProgressListener(new ProgressListener() {
			
			@Override
			public void completed(ProgressEvent event) {
				browser.execute("window.scrollBy(0,-50)");
			}
			
			@Override
			public void changed(ProgressEvent event) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	@Inject
	@Optional
	private void pursueNorm(@UIEventTopic(Event.PURSUE_NORM) 
	    IPursuedConclusion norms) {
		pursuedNorms = norms;
		// setText(norm);
	} 
	
	private void selectNorm(INorm norm) {
		if (norm != null) {
			this.norm = norm;
			txtLabel.setText(norm.getLabel());
			txtLabel.setImage(new NormDecorator(norm).createImage(pursuedNorms).createImage());
			browser.setUrl(norm.getUrl());
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

	@Focus
	public void setFocus() {
		
	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}
}
