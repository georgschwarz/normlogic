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
import javax.swing.plaf.metal.MetalButtonUI;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.normlogic.navigator.app.BottomUpTreeLayoutAlgorithm;
import org.normlogic.navigator.app.SimpleLayoutAlgorithm;

public class LayoutHandler {
	
    private static final RadialLayoutAlgorithm RADIAL = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
    private static final TreeLayoutAlgorithm VERTICAL = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
    private static final HorizontalTreeLayoutAlgorithm HORIZONTAL = new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
    private static final BottomUpTreeLayoutAlgorithm BOTTOMUP = new BottomUpTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
    private static final SimpleLayoutAlgorithm SIMPLE = new SimpleLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
    
    private static final LayoutAlgorithm DEFAULT = HORIZONTAL; 
        
	private GraphViewer viewer = null;
	
	@PostConstruct
	public void initialize(Composite parent) {
		if (parent.getData() instanceof GraphViewer) {
			viewer = (GraphViewer)parent.getData();
		}
	}

	
	@Execute
	public void execute(MDirectMenuItem item) {
		LayoutAlgorithm layout = DEFAULT;
		item.setIconURI("");
		switch (item.getElementId()) {
		case "org.normlogic.navigator.app.directmenuitem.horizontalLayout":
			layout = HORIZONTAL;
			break;
		case "org.normlogic.navigator.app.directmenuitem.topDown":
			layout = VERTICAL;
			break;
		case "org.normlogic.navigator.app.directmenuitem.bottomUp":
			layout = BOTTOMUP;
			break;
		case "org.normlogic.navigator.app.directmenuitem.radial":
			layout = RADIAL;
			break;
		case "org.normlogic.navigator.app.directmenuitem.simple":
			layout = SIMPLE;
			break;
		case "org.normlogic.navigator.app.directtoolitem.layout":
			layout = DEFAULT;
			break;
		}
		viewer.setLayoutAlgorithm(layout);
		viewer.applyLayout();
	}

}
