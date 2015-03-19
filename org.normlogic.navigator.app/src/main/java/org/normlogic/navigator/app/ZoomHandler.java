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
package org.normlogic.navigator.app;

import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.viewers.internal.ZoomManager;

public class ZoomHandler {

	/*
	ZoomManager zoom;
	// GraphViewer viewer;
	
	@PostConstruct
	public void initialize(ZoomManager zoom) {
		this.zoom = zoom;
	 }
	*/
	
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items, ZoomManager zoom) {
	    
	    String[] levels = zoom.getZoomLevelsAsText();
	    for (int i = 0; i<levels.length; i++) {
	    	MDirectMenuItem dynamicItem = MMenuFactory.INSTANCE.createDirectMenuItem();
	    	dynamicItem.setLabel(levels[i]);
	    	dynamicItem.setType(ItemType.RADIO);
	    	dynamicItem.setContributionURI("bundleclass://org.normlogic.navigator.app/org.normlogic.navigator.app.ZoomHandler");
	        items.add(dynamicItem);
	    }
	}

	@Execute
	public void execute(MDirectMenuItem item, ZoomManager zoom) {
		if (item.isSelected()) {
			zoom.setZoomAsText(item.getLabel());
		}
	}
	
}
