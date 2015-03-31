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

import java.util.Set;
import java.util.TreeSet;

import org.normlogic.navigator.core.IConcept;
import org.normlogic.navigator.core.IHierarchy;

public class ConceptHierarchy implements IHierarchy<IConcept> {

	KnowledgeBase kb;
	Set<IConcept> concepts;
	
	public ConceptHierarchy(KnowledgeBase kb, Set<IConcept> concepts) {
		this.kb = kb;
		this.concepts = concepts;
	}

	@Override
	public Set<IConcept> getTopLevelEntities() {
		Set<IConcept> result = new TreeSet<>();
		result.addAll(kb.getTopLevelConceptsOf(concepts));
		return result;
	}

	@Override
	public Set<IConcept> getDirectChildEntitesOf(IConcept entity) {
		Set<IConcept> subConcepts = entity.getSubConcepts(true);
		subConcepts.retainAll(concepts);
		return subConcepts;
	}

	private Set<IConcept> getLeafEntities(IConcept concept) {
		Set<IConcept> leafConcepts = new TreeSet<>();
		Set<IConcept> subConcepts = concept.getSubConcepts(true);
		subConcepts.retainAll(concepts);
		if (subConcepts.isEmpty()) {
			leafConcepts.add(concept);
		}
		else {
			for (IConcept subConcept : subConcepts) {
				leafConcepts.addAll(getLeafEntities(subConcept));
			}
		}
		return leafConcepts;
	}
	
	@Override
	public Set<IConcept> getLeafEntities() {
		Set<IConcept> leafConcepts = new TreeSet<>();
		for (IConcept concept : concepts) {
			leafConcepts.addAll(getLeafEntities(concept));
		}
		return leafConcepts;
	}

	@Override
	public Set<IConcept> getEntites() {
		return concepts;
	}
}
