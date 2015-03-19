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

import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.INorm;

public class Norm extends ModelEntity implements INorm {

	public Norm(KnowledgeBase kb, Object norm) {
		super(kb, norm);
	}

	@Override
	public String getLabel() {
		return kb.getLabelOf(this);
	}

	@Override
	public int compareTo(INorm o) {
		return getLabel().compareTo(o.getLabel());
	}

	@Override
	public String getText() {
		return kb.getTextOf(this);
	}

	@Override
	public boolean isFulfilledFor(IIndividual individual) {
		return kb.isNormFullfilled(this, individual);
	}

	@Override
	public boolean isFulfilled() {
		return kb.isNormFullfilled(this);
	}

	@Override
	public boolean hasToBeFulfilled() {
		return kb.hasNormToBeFullfilled(this);
	}

	@Override
	public boolean hasToBeFulfilledFor(IIndividual individual) {
		return kb.hasNormToBeFullfilled(this, individual);
	}
}
