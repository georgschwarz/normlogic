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
package org.normlogic.navigator.core.impl;

import org.normlogic.navigator.core.IProperty;

public class Property extends ModelEntity implements IProperty {

	public Property(KnowledgeBase kb, Object property) {
		super(kb, property);
	}

	@Override
	public String getLabel() {
		return kb.getLabelOf(this);
	}
}
