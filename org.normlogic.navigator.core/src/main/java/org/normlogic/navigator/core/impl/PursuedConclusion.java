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
import org.normlogic.navigator.core.IHierarchy;
import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.INeighborhoodViewer;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IProperty;
import org.normlogic.navigator.core.IPursuedConclusion;

public class PursuedConclusion implements IPursuedConclusion {
	
	Set<INorm> norms;
	IIndividual individual;
	IProperty property;
	IConcept concept;
	boolean isAccomplished = false;
	KnowledgeBase kb;
	
	PursuedConclusion(IIndividual individual, IProperty property, IConcept concept, Set<INorm> norms, KnowledgeBase kb) {
		this.individual = individual;
		this.property = property;
		this.concept = concept;
		this.norms = norms;
		this.kb = kb;
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

	@Override
	public boolean dependsOn(IIndividual individual, IProperty property,
			IConcept concept) {
		if (isAccomplished) return false;
		return kb.dependNormedConclusionOnTriple(individual, norms, property, concept);
	}

	@Override
	public boolean dependsOn(final IIndividual individual) {
		if (isAccomplished) return false;
		final Set<Boolean> result = new HashSet<>();
		individual.renderNeighborhood(kb.getNormedWorld(), new INeighborhoodViewer() {
			@Override
			public void add(IProperty property, IHierarchy<IConcept> concepts) {
				for (IConcept concept : concepts.getLeafEntities()) {
					result.add(new Boolean(kb.dependNormedConclusionOnTriple(individual, norms, property, concept)));
				}
			}
		});
		return result.contains(new Boolean(true));
	}
}
