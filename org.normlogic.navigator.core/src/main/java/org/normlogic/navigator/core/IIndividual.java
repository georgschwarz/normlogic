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

package org.normlogic.navigator.core;

import java.util.Set;

/**
 * Represents an individual in the knowledgebase as a member of the a-box of the
 * DL-system.
 * 
 * @author Georg Schwarz
 *
 */
public interface IIndividual {
	/**
	 * Returns a human readable name of the individual
	 * 
	 * @return	human readalbe name as a string.
	 */
	String getLabel();
	
	/**
	 * Finds the possible relation, an individual can have in the given normed world. All
	 * concepts an individual is assigned to, are considered, including all assignments found
	 * by the reasoner. 
	 * 
	 * @param world		the given normed world 
	 * @param viewer	a viewer object, which handles the found relations.
	 */
	void renderNeighborhood(INormedWorld world, INeighborhoodViewer viewer);
	
	/**
	 * Removes the individual from the knowledgebase
	 */
	void deleteFromKnowledgeBase();
	
	/**
	 * A convenient method to add an triple assertion to this individual. A relation to a newly
	 * created individual is assigned. The newly created individual will be named interactively by
	 * using the given IAddEntityDialog.
	 * 
	 * @param property	the property of the triple
	 * @param concept	the range concept of the triple
	 * @param dialog	the dialog object to name the newly created individual
	 * @return			true if the assertion succeeds and is consistent with the knowledgebase,
	 * 					false if not. 
	 */
	boolean addAssertionFromConcept(IProperty property, IConcept concept,
			IAddEntityDialog dialog);
	
	/**
	 * Makes a look up if a triple assertion is possible, specifically if the assertion is
	 * consistent with the current knowledgebase.
	 * 
	 * @param property	the property of the triple
	 * @param concept	the range concept of the triple
	 * @return			true if the assertion is possible in a consistent way, false if not
	 */
	boolean isAssertableWithConcept(IProperty property, IConcept concept);
	
	/**
	 * Checks if an assertion to the typed triple exists. 
	 * 
	 * @param property	the property of the triple
	 * @param concept	the range concept of the triple
	 * @return			true if the assertion exists, false if not.
	 */
	boolean hasAssertion(IProperty property, IConcept concept);
	
	/**
	 * A convenient method to get the norms, in which the individual in respect to a given
	 * abstract triple is part of the condition.
	 * 
	 * @param world		the given normed world
	 * @param property	the property of the triple
	 * @param concept	the concept of the triple
	 * @return			set of norms
	 */
	Set<INorm> getContextNorms(INormedWorld world, IProperty property, IConcept concept);
	
	/**
	 * A convenient method to get the norms, in which the individual in respect to a given
	 * abstract triple is part of the conclusion.
	 * 
	 * @param world		the given normed world
	 * @param property	the property of the triple
	 * @param concept	the concept of the triple
	 * @return			set of norms
	 */
	Set<INorm> getObligationNorms(INormedWorld world, IProperty property, IConcept concept);
	
	/**
	 * A convenient method to get the norms, in which the individual is part of the condition
	 * of the given normed world.
	 * 
	 * @param world		the given normed world
	 * @return			set of norms
	 */
	Set<INorm> getContextNorms(INormedWorld world);
	
	/**
	 * A convenient method to get the norms, in which the individual is part of the conclusion
	 * of the given normed world.
	 * 
	 * @param world		the given normed world
	 * @return			set of norms
	 */
	Set<INorm> getObligationNorms(INormedWorld world);
	
	/**
	 * Pursue the given norms in respect to this individual. 
	 * 
	 * @param norms		norms to pursue
	 * @return			the created IPursuedNorms object
	 */
	IPursuedConclusion pursue(IProperty property, IConcept concept, Set<INorm> norms);
	
	/**
	 * Checks if a potential assertion triple is relevant in respect to the
	 * pursued norms.
	 * 
	 * @param pursuedNorms	the IPursuedNorms object
	 * @param property		the property of the potential relation triple
	 * @param concept		the range concept of the potential relation triple
	 * @return				true if the assertion triple is relevant, false if not.
	 */
	boolean hasPursuedTriple(IPursuedConclusion pursuedNorms, IProperty property, IConcept concept);
	
	/**
	 * Recalculated the asserted types of this individual.
	 */
	void updateTypes();
	
	/**
	 * Get the types of this individual
	 * 
	 * @return types as an IConcept hierarchy 
	 */
	IHierarchy<IConcept> getTypes();

	/**
	 * Assert individual to an explicit type
	 * 
	 * @param concept addition type of individual
	 * @return	true is assertion succeeded, false if not.
	 */
	boolean assertType(final IConcept concept);
}
