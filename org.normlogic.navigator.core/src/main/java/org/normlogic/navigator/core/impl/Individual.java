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
import org.normlogic.navigator.core.INeighborhoodViewer;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IProperty;
import org.normlogic.navigator.core.IPursuedNorms;

public class Individual extends ModelEntity implements IIndividual {

	public Individual(KnowledgeBase kb, Object individual) {
		super(kb, individual);
	}

	@Override
	public void renderNeighborhood(INormedWorld world, INeighborhoodViewer viewer) {
		if (world instanceof NormedWorld) {
			NormedWorld normedWorld = (NormedWorld)world;
			Set<IConcept> types = kb.getAssertedTypes(this);
			for (IConcept type : types) {
				Set<IProperty> properties = normedWorld.getPropertiesForDomain(type);
				for (IProperty property : properties) {
					Set<IConcept> ranges = normedWorld.getRangesForDomainProperty(type, property);
					viewer.add(property, new ConceptHierarchyMaker(kb, ranges));
				}
			}
		}
	}

	@Override
	public void deleteFromKnowledgeBase() {
		kb.delete(this);		
	}

	@Override
	public boolean addAssertionFromConcept(IProperty property, IConcept concept,
		IAddEntityDialog dialog) {

		IIndividual object = concept.asIndividual(dialog);
		return kb.addIndividualAssertion(this, property, object, concept);
	}

	@Override
	public boolean isAssertableWithConcept(IProperty property, IConcept concept) {
		return kb.isIndividualAssertableWithConcept(this, property, concept);
	}

	@Override
	public Set<INorm> getContextNorms(INormedWorld world, IProperty property, IConcept concept) {
		Set<INorm> result = new HashSet<>();
		if (world instanceof NormedWorld) {
			NormedWorld normedWorld = (NormedWorld)world;
			Set<IConcept> types = kb.getAssertedTypes(this);
			for (IConcept domain : types) {
				result.addAll(normedWorld.getNormsFor(new WorldTriple(domain, property, concept), NormedWorld.ContextType.CONDITION));
			}
		}
		return result;
	}

	@Override
	public Set<INorm> getObligationNorms(INormedWorld world, IProperty property, IConcept concept) {
		Set<INorm> result = new HashSet<>();
		if (world instanceof NormedWorld) {
			NormedWorld normedWorld = (NormedWorld)world;
			Set<IConcept> types = kb.getAssertedTypes(this);
			for (IConcept domain : types) {
				result.addAll(normedWorld.getNormsFor(new WorldTriple(domain, property, concept), NormedWorld.ContextType.OBLIGATION));
			}
		}
		return result;
	}

	@Override
	public String getLabel() {
		return kb.getLabelOf(this);
	}

	@Override
	public Set<INorm> getContextNorms(INormedWorld world) {
		Set<INorm> result = new HashSet<>();
		if (world instanceof NormedWorld) {
			NormedWorld normedWorld = (NormedWorld)world;
			Set<IConcept> types = kb.getAssertedTypes(this);
			for (IConcept domain : types) {
				result.addAll(normedWorld.getNormsFor(domain, NormedWorld.ContextType.CONDITION));
			}
		}
		return result;
	}

	@Override
	public Set<INorm> getObligationNorms(INormedWorld world) {
		Set<INorm> result = new HashSet<>();
		if (world instanceof NormedWorld) {
			NormedWorld normedWorld = (NormedWorld)world;
			Set<IConcept> types = kb.getAssertedTypes(this);
			for (IConcept domain : types) {
				result.addAll(normedWorld.getNormsFor(domain, NormedWorld.ContextType.OBLIGATION));
			}
		}
		return result;
	}

	@Override
	public IPursuedNorms pursue(Set<INorm> norms) {
		return new PursuedNorms(this, norms);
	}

	@Override
	public boolean hasAssertion(IProperty property, IConcept concept) {
		return kb.hasIndividualAssertion(this, property, concept);
	}

	@Override
	public boolean hasPursuedTriple(IPursuedNorms pursuedNorms, IProperty property, IConcept concept) {
		return kb.hasIndividualPursuedTriple(this, pursuedNorms.getNorms(), property, concept);
	}
}
