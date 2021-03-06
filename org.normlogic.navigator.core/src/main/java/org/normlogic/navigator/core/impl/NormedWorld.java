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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.normlogic.navigator.core.IConcept;
import org.normlogic.navigator.core.IExpression;
import org.normlogic.navigator.core.IHierarchy;
import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IOntology;
import org.normlogic.navigator.core.IProperty;
import org.normlogic.navigator.core.IWorldTriple;
import org.normlogic.navigator.core.impl.NormContext.Type;

public class NormedWorld implements INormedWorld {
	
	private Map<IOntology, Set<INorm>> normsByOntology = new HashMap<>();
	private KnowledgeBase kb;
    
	public NormedWorld(KnowledgeBase kb) {
		this.kb = kb;
	}
	
	public Set<INorm> getNormsOf(IOntology ontology) {
		if (normsByOntology.containsKey(ontology)) {
			return normsByOntology.get(ontology);
		}
		else return new HashSet<INorm>();
	}

	Set<WorldTriple> triples = new HashSet<>();
	Map<IConcept, Set<WorldTriple>> triplesByDomain = new HashMap<>();
	Map<IProperty, Set<WorldTriple>> triplesByProperty = new HashMap<>();
	Map<IConcept, Set<WorldTriple>> triplesByRange = new HashMap<>();
	
	Map<WorldTriple, Set<NormContext>> normsByTriple = new HashMap<>();
	Map<NormContext, Set<WorldTriple>> triplesByNormContext = new HashMap<>();
	
	Map<NormedTriple, IExpression> expressionByNormedTriple = new HashMap<>();
	
	class NormedTriple {
		IWorldTriple triple;
		NormContext normContext;
		NormedTriple(IWorldTriple triple, NormContext normContext) {
			this.triple = triple;
			this.normContext = normContext;
		}
	   @Override
	    public int hashCode() {
	        int hash = 7;
	        hash = 17 * hash + ((triple == null) ? 0 : triple.hashCode());
	        hash = 17 * hash + ((normContext == null) ? 0 : normContext.hashCode());
	        return hash;
	    }
	    @Override
	    public boolean equals(final Object obj) {
	        if (this == obj) return true;
	        if (obj == null) return false;
	        if (getClass() != obj.getClass()) return false;
	        NormedTriple other = (NormedTriple)obj;
	        if (triple == null && other.triple != null) return false;
	        if (normContext == null && other.normContext != null) return false;
	        if (!triple.equals(other.triple)) return false;
	        if (!normContext.equals(other.normContext)) return false;
	        return true;
	    }
	}

	
	public IWorldTriple addWorldTriple(WorldTriple newTriple) {
		// add triple
		triples.add(newTriple);
		// create index maps
		if  (!triplesByDomain.containsKey(newTriple.domain)) {
			triplesByDomain.put(newTriple.domain, new HashSet<WorldTriple>());
		}
		if (!triplesByProperty.containsKey(newTriple.property)) {
			triplesByProperty.put(newTriple.property, new HashSet<WorldTriple>());
		}
		if (!triplesByRange.containsKey(newTriple.range)) {
			triplesByRange.put(newTriple.range, new HashSet<WorldTriple>());
		}
		triplesByDomain.get(newTriple.domain).add(newTriple);
		triplesByProperty.get(newTriple.property).add(newTriple);
		triplesByRange.get(newTriple.range).add(newTriple);
		return newTriple;
	}
	
	public void mapNormsToWorldTriple(WorldTriple triple, Set<NormContext> normContext, IExpression expression) {
		if (!normsByTriple.containsKey(triple)) {
			normsByTriple.put(triple, new HashSet<NormContext>());
		}
		normsByTriple.get(triple).addAll(normContext);

		for (NormContext singleContext : normContext) {
			if (!triplesByNormContext.containsKey(singleContext)) {
				triplesByNormContext.put(singleContext,  new HashSet<WorldTriple>());
			}
			triplesByNormContext.get(singleContext).add(triple);
		}
		
		for (NormContext nc : normContext) {
			NormedTriple normedTriple = new NormedTriple(triple, nc);
			expressionByNormedTriple.put(normedTriple, expression);
		}
	}
	
	Set<IProperty> getPropertiesForDomain(IConcept domain) {
		Set<IProperty> result = new HashSet<>(); 
		Set<WorldTriple> triples = triplesByDomain.get(domain);
		if (triples != null) {
			for (WorldTriple triple : triples) {
				result.add(triple.property);
			}
		}
		return result;
	}
	
	Set<IConcept> getRangesForDomainProperty(IConcept domain, IProperty property) {
		Set<IConcept> result = new HashSet<>();
		Set<WorldTriple> domainTriples = triplesByDomain.get(domain);
		Set<WorldTriple> propertyTriples = triplesByProperty.get(property);
		if (domainTriples != null && propertyTriples != null) {
			Set<WorldTriple> mergerSet = new HashSet<>();
			mergerSet.addAll(domainTriples);
			mergerSet.retainAll(propertyTriples);
			for (WorldTriple triple : mergerSet) {
				result.add(triple.range);
			}
		}
		return result;
	}
	
	Set<INorm> getNormsFor(IWorldTriple triple, Type type) {
		Set<INorm> norms = new HashSet<>();
		Set<NormContext> normedContext = normsByTriple.get(triple);
		if (normedContext != null) {
			for (NormContext context : normedContext) {
				if (context.type == type) {
					norms.add(context.norm);
				}
			}
		}
		return norms;
	}
	
	Set<INorm> getNormsFor(IConcept concept, Type type, boolean includeRange) {
		Set<INorm> norms = new HashSet<>();
		Set<WorldTriple> triples = new HashSet<>();
		Set<WorldTriple> triplesDomain = triplesByDomain.get(concept);
		if (triplesDomain != null) triples.addAll(triplesDomain);
		if (includeRange) {
			Set<WorldTriple> triplesRange = triplesByRange.get(concept);
			if (triplesRange != null) triples.addAll(triplesRange);
		}
		for (IWorldTriple triple : triples) {
			norms.addAll(getNormsFor(triple, type));
		}
		return norms;
	}
	
	public IExpression getExpressionFor(IWorldTriple triple, NormContext context) {
		return expressionByNormedTriple.get(new NormedTriple(triple, context));
	}

	public void mapNormToOntology(IOntology ontology, INorm norm) {
		if (!normsByOntology.containsKey(ontology)) {
			normsByOntology.put(ontology, new TreeSet<INorm>());
		}
		normsByOntology.get(ontology).add(norm);
	}
	
	@Override
	public IHierarchy<IConcept> getDomains() {
		return new ConceptHierarchy(kb, triplesByDomain.keySet());
	}

	Set<WorldTriple> getTriplesForPropertyRange(IProperty property,
			IConcept concept) {
		Set<WorldTriple> triples = new HashSet<>();
		triples.addAll(triplesByProperty.get(property));
		triples.retainAll(triplesByRange.get(concept));
		return triples;
	}
	
	public Set<WorldTriple> getTriplesForNormContext(NormContext normContext) {
		Set<WorldTriple> result = triplesByNormContext.get(normContext);
		if (result == null) {
			return new HashSet<WorldTriple>();
		}
		return result; 
	}

	@Override
	public boolean contains(IConcept concept) {
		return (triplesByDomain.containsKey(concept) || triplesByRange.containsKey(concept));
	}

	@Override
	public IHierarchy<IConcept> retainIncluded(final Set<IConcept> concepts) {
		Set<IConcept> includedConcepts = new HashSet<>();
		includedConcepts.addAll(triplesByDomain.keySet());
		includedConcepts.addAll(triplesByRange.keySet());
		includedConcepts.retainAll(concepts);
		return new ConceptHierarchy(kb, includedConcepts);
	}
}
