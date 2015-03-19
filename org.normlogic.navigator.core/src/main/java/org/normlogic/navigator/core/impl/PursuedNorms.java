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

import org.normlogic.navigator.core.IConcept;
import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IProperty;
import org.normlogic.navigator.core.IPursuedNorms;

public class PursuedNorms implements IPursuedNorms {
	
	Set<INorm> norms;
	IIndividual individual;
	boolean isAccomplished = false;
	
	PursuedNorms(IIndividual individual, Set<INorm> norms) {
		this.individual = individual;
		this.norms = norms;
		update();
	}

	@Override
	public void update() {
		for (INorm norm : norms) {
			if (norm.isFulfilledFor(individual) || norm.hasToBeFulfilledFor(individual)) {
				isAccomplished = true;
				return;
			}
		}
		isAccomplished = false;
	}

	@Override
	public boolean relevantFor(INormedWorld world, IIndividual individual, IProperty property, IConcept concept) {
		if (isAccomplished) return false;
		Set<INorm> normsContext = individual.getContextNorms(world, property, concept);
		normsContext.retainAll(norms);
		return !normsContext.isEmpty();
	}
	
	@Override
	public boolean relevantFor(INormedWorld world, IIndividual individual) {
		if (isAccomplished) return false;
		Set<INorm> normsContext = individual.getContextNorms(world);
		normsContext.retainAll(norms);
		return !normsContext.isEmpty();
	}

	@Override
	public boolean contains(INorm norm) {
		if (isAccomplished) return false;
		return norms.contains(norm);
	}

	@Override
	public Set<INorm> getNorms() {
		if (norms==null) {
			norms = new HashSet<>();
		}
		return norms;
	}
	
		
}
