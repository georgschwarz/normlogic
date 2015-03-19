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
package org.normlogic.navigator.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.normlogic.navigator.parts.LibraryPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class IconPool {
	
	public final static ImageDescriptor overlayStar = createImage("icons/8/important.png");
	public final static ImageDescriptor overlayCaution = createImage("icons/16/caution.png");
	public final static ImageDescriptor overlayOk = createImage("icons/16/ok.png");
	
	public final static ImageDescriptor overlayObligation = createImage("icons/16/conclusion.png");
	
	public final static ImageDescriptor ontologyDefault = createImage("icons/16/book.gif");
	
	
	// icons to express the normative state on an triple
	public final static ImageDescriptor tripleInContext = createImage("icons/16/context.png");
	public final static ImageDescriptor tripleInConclusion = createImage("icons/16/hasconclusion.png");
	public final static ImageDescriptor tripleHasToBeFullilled = new DecorationOverlayIcon(tripleInContext.createImage(), overlayCaution, IDecoration.BOTTOM_RIGHT);
	public final static ImageDescriptor tipleIsFulliflled = new DecorationOverlayIcon(tripleInContext.createImage(), overlayOk, IDecoration.BOTTOM_RIGHT);
	
	
	// icons to express the state of a norm
	public final static ImageDescriptor normDefault = createImage("icons/16/normDefault.png");
	public final static ImageDescriptor normIsFullfilled = new DecorationOverlayIcon(normDefault.createImage(), overlayOk, IDecoration.BOTTOM_RIGHT);
	public final static ImageDescriptor normHasToBeFullfilled = new DecorationOverlayIcon(normDefault.createImage(), overlayCaution, IDecoration.BOTTOM_RIGHT);

	
	final static ImageDescriptor createImage(String path) {
	    Bundle bundle = FrameworkUtil.getBundle(LibraryPart.class);
	    URL url = FileLocator.find(bundle, new Path(path), null);
	    return ImageDescriptor.createFromURL(url);
	}
}
