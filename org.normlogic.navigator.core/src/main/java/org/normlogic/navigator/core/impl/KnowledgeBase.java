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

import org.normlogic.navigator.core.IAssertionTriple;
import org.normlogic.navigator.core.IConcept;
import org.normlogic.navigator.core.IIndividual;
import org.normlogic.navigator.core.IKnowledgeBase;
import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.INormedWorld;
import org.normlogic.navigator.core.IOntology;
import org.normlogic.navigator.core.IProperty;

public abstract class KnowledgeBase implements IKnowledgeBase {
	protected abstract String getLabelOf(IConcept concept);
	protected abstract String getLabelOf(IIndividual individual);
	protected abstract String getLabelOf(IProperty property);
	protected abstract String getLabelOf(INorm norm);
	protected abstract String getLabelOf(IAssertionTriple triple);
	protected abstract String getLabelOf(IOntology ontology);
	protected abstract Set<IConcept> getSubClassesOf(IConcept concept, boolean direct);
	protected abstract Set<IConcept> getSuperClassesOf(IConcept concept, boolean direct);
	protected abstract Set<IConcept> getTopLevelConceptsOf(Set<IConcept> concepts);
	protected abstract void delete(IIndividual individual);
	protected abstract boolean addIndividualAssertion(IIndividual subject, IProperty property, IIndividual object, IConcept concept);
	protected abstract boolean addIndividualAssertion(IIndividual individual, IConcept concept);
	protected abstract boolean isIndividualAssertableWithTriple(IIndividual individual, IProperty property, IConcept concept);
	protected abstract IIndividual getIndividual(String name);
	protected abstract IIndividual getSubjectOfTriple(IAssertionTriple triple);
	protected abstract IIndividual getObjectOfTriple(IAssertionTriple triple);
	protected abstract Set<IConcept> getAssertedTypes(IIndividual individual);
	protected abstract Set<INorm> getNorms(Ontology ontology);
	protected abstract String getTextOf(Norm norm);
	protected abstract boolean isNormFullfilled(Norm norm, IIndividual individual);
	protected abstract boolean isNormFullfilled(INorm norm);
	protected abstract boolean hasNormToBeFullfilled(INorm norm);
	protected abstract boolean hasNormToBeFullfilled(INorm norm, IIndividual individual);
	protected abstract boolean hasIndividualAssertion(IIndividual individual, IProperty property, IConcept concept);
	protected abstract boolean dependNormedConclusionOnTriple(IIndividual individual, Set<INorm> norms, IProperty property, IConcept concept);
	protected abstract void updateTypes(final Individual individual, final Set<IConcept> types);
	protected abstract boolean assertType(Individual individual, IConcept concept);
	protected abstract boolean removeType(final Individual individual, final IConcept concept);
	protected abstract boolean isIndividualAssertableWithType(final Individual individual, final IConcept concept);
	protected abstract boolean checkForNegativeType(final IIndividual individual, final IConcept concept);
	protected abstract boolean isSubClassOf(final IConcept concept, final Set<IConcept> types);
	public abstract NormedWorld getNormedWorld();
}