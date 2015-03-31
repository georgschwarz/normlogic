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
import java.util.TreeSet;

import org.normlogic.navigator.core.IAddEntityDialog;
import org.normlogic.navigator.core.IConcept;
import org.normlogic.navigator.core.IHierarchy;
import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.INeighborhoodViewer;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IProperty;
import org.normlogic.navigator.core.IPursuedConclusion;

public class Individual extends ModelEntity implements IIndividual {

	final Set<IConcept> types = new HashSet<>();
	
	public Individual(KnowledgeBase kb, Object individual) {
		super(kb, individual);
	}

	@Override
	public void renderNeighborhood(INormedWorld world, INeighborhoodViewer viewer) {
		if (world instanceof NormedWorld) {
			NormedWorld normedWorld = (NormedWorld)world;
			// TODO: check if asserted types have to be reasoned...
			Set<IConcept> types = kb.getAssertedTypes(this);
			types = normedWorld.retainIncluded(types).getEntites();
			Set<IProperty> properties = new TreeSet<>();
			for (IConcept type : types) {
				properties.addAll(normedWorld.getPropertiesForDomain(type));
			}
			for (IProperty property : properties) {
				Set<IConcept> ranges = new TreeSet<>();
				for (IConcept type : types) {
					ranges.addAll(normedWorld.getRangesForDomainProperty(type, property));
				}
				viewer.add(property, new ConceptHierarchy(kb, ranges));
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
	public IPursuedConclusion pursue(IProperty property, IConcept concept, Set<INorm> norms) {
		return new PursuedConclusion(this, property, concept, norms, kb);
	}

	@Override
	public boolean hasAssertion(IProperty property, IConcept concept) {
		return kb.hasIndividualAssertion(this, property, concept);
	}

	@Override
	public boolean hasPursuedTriple(IPursuedConclusion pursuedConclusion, IProperty property, IConcept concept) {
		return kb.dependNormedConclusionOnTriple(this, pursuedConclusion.getNorms(), property, concept);
	}

	@Override
	public void updateTypes() {
		kb.updateTypes(this, types);
	}

	@Override
	public IHierarchy<IConcept> getTypes() {
		return new ConceptHierarchy(kb, types);
	}

	@Override
	public boolean assertType(final IConcept concept) {
		return kb.assertType(this, concept);		
	}
}
