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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.IPursuedConclusion;

public class NormDecorator {
	INorm norm;
	public NormDecorator(INorm norm) {
		this.norm = norm;
	}
	public ImageDescriptor createImage(IPursuedConclusion pursuedNorms) {
		ImageDescriptor baseImage = IconPool.normDefault;
		if (norm.hasToBeFulfilled()) {
			baseImage = IconPool.normHasToBeFullfilled;
		}
		else if (norm.isFulfilled()) {
			baseImage = IconPool.normIsFullfilled;
		}
		if (pursuedNorms != null) {
			if (pursuedNorms.contains(norm)) {
				baseImage = new DecorationOverlayIcon(baseImage.createImage(), IconPool.overlayStar, IDecoration.BOTTOM_RIGHT);
			}
		}
		return baseImage;
	}
}
