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
import org.eclipse.swt.graphics.ImageData;
import org.normlogic.navigator.core.INorm;

public class NormImage extends ImageDescriptor {
	ImageDescriptor image = IconPool.normDefault;
	public NormImage(INorm norm) {
		if (norm.isFulfilled()) {
			image = IconPool.normIsFullfilled;
		}
		else if (norm.hasToBeFulfilled()) {
			image = IconPool.normHasToBeFullfilled;
		}
	}
	@Override
	public ImageData getImageData() {
		return image.getImageData();
	}
}
