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
import org.normlogic.navigator.core.IHierarchyMaker;

public class ConceptHierarchyMaker implements IHierarchyMaker<IConcept> {

	KnowledgeBase kb;
	Set<IConcept> concepts;
	
	public ConceptHierarchyMaker(KnowledgeBase kb, Set<IConcept> concepts) {
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

}
