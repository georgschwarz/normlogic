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

import org.normlogic.navigator.core.IAssertionTriple;
import org.normlogic.navigator.core.IIndividual;



public class IndividualAssertionTriple extends ModelEntity implements IAssertionTriple {

	public IndividualAssertionTriple(KnowledgeBase kb, Object axiom) {
		super(kb, axiom);
	}
	
	@Override
	public IIndividual getTarget() {
		return kb.getSubjectOfTriple(this);
	}

	@Override
	public IIndividual getSource() {
		return kb.getObjectOfTriple(this);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return kb.getLabelOf(this);
	}
}
