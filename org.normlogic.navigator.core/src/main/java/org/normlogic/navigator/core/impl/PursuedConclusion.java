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
	public String toString() {
		String text = new String();
		text = individual.getLabel() + ": " + property.getLabel() + " -> " + concept.getLabel();
		/*
		for (INorm norm : norms) {
			text = text + "\n" + norm.getLabel();
		}
		*/
		return text;
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
	public boolean relevantFor(INormedWorld world, IIndividual individual) {
		if (isAccomplished) return false;
		Set<INorm> normsContext = individual.getContextNorms();
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
		if (!kb.dependNormedConclusionOnTriple(individual, norms, property, concept)) {
			return false;
		}
		if (individual.hasAssertion(property, concept)) {
			return false;
		}
		NormedWorld world = kb.getNormedWorld();
		Set<IConcept> superConcepts = world.retainIncluded(kb.getSuperClassesOf(concept, false)).getEntites();
		for (IConcept superConcept : superConcepts) {
			if (individual.hasAssertion(property, superConcept)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean dependsOn(final IIndividual individual) {
		if (isAccomplished) return false;
		final Set<Boolean> result = new HashSet<>();
		individual.renderNeighborhood(kb.getNormedWorld(), new INeighborhoodViewer() {
			@Override
			public void add(IProperty property, IHierarchy<IConcept> concepts) {
				for (IConcept concept : concepts.getEntites()) {
					result.add(new Boolean(dependsOn(individual, property, concept)));
				}
			}
		});
		return result.contains(new Boolean(true));
	}
	
	public boolean dependsON(final IIndividual individual, final IConcept subType) {
		if (isAccomplished) return false;
		Set<IConcept> concepts = new HashSet<>();
		for (INorm norm : norms) {
			Set<WorldTriple> triples = kb.getNormedWorld().getTriplesForNormContext(new NormContext(norm, NormContext.CONDITION));
			for (WorldTriple triple: triples) {
				concepts.add(triple.domain);
				concepts.add(triple.range);
			}
		}
		if (concepts.contains(subType)) {
			final IHierarchy<IConcept> types = individual.getTypes();
			if (subType.isSubClassOf(types.getEntites())) {
				return true;
			}
		}
		return false;
	}
}
