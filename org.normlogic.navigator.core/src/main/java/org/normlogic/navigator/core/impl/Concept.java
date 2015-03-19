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

import java.util.HashSet;
import java.util.Set;

import org.normlogic.navigator.core.IAddEntityDialog;
import org.normlogic.navigator.core.IConcept;
import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;

public class Concept extends ModelEntity implements IConcept {

	Object concept;	
	public Concept(KnowledgeBase kb, Object concept) {
		super(kb, concept);
		this.concept = concept;
	}

	@Override
	public Set<IConcept> getSubConcepts(boolean direct) {
		return kb.getSubClassesOf(this, direct);
	}
	
	public String getLabel() {
		return kb.getLabelOf(this);
	}
	
	@Override
	public IIndividual asIndividual(IAddEntityDialog dialog) {
		String name = dialog.runDialog(getLabel());
		if (name == null) {
			return kb.getIndividual("ups");
		}
		return kb.getIndividual(name);
	}
	
	@Override
	public IIndividual createAssertedIndividual(IAddEntityDialog dialog) {
		String name = dialog.runDialog(getLabel());
		if (name == null) {
			return kb.getIndividual("ups");
		}
		IIndividual individual = kb.getIndividual(name);
		kb.addIndividualAssertion(individual, this);
		return individual;
	}

	@Override
	public int compareTo(IConcept o) {
		return getLabel().compareTo(o.getLabel());
	}

	@Override
	public Set<INorm> getObligationNorms(INormedWorld currentWorld) {
		Set<INorm> result = new HashSet<>();
		if (currentWorld instanceof NormedWorld) {
			result.addAll(((NormedWorld) currentWorld).getNormsFor(this, NormedWorld.ContextType.OBLIGATION));
		}
		return result;
	}
}
