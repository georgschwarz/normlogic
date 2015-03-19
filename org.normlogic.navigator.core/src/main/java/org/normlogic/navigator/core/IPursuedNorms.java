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
 * Represents pursued norms in respect to an individual. If a norm is
 * pursued, relations and individual can be checked for their relevance
 * subject to the pursued norms. 
 * 
 * @author Georg Schwarz
 *
 */
public interface IPursuedNorms {
	/**
	 * Updates the states of the norms in respect to the individual of the
	 * pursued norms, specifically if pursued norms are already fulfilled or 
	 * has to be fulfilled for this individual.
	 */
	void update();

	/**
	 * Checks if the given norm is included in the pursued norms.
	 * 
	 * @param norm	norm to be checked.
	 * @return		true if norm is included, false if not
	 */
	boolean contains(INorm norm);
	
	/**
	 * Checks if a given relation subject to an individual is relevant for the
	 * fulfillment of the pursued norms.
	 * 
	 * @param world			normed world in which the checked relation resides
	 * @param individual	individual which is base of the checked relation
	 * @param property		property of the relation triple
	 * @param concept		range concept of the relation triple
	 * @return				true if the given relation is relevant to fulfill the pursued norms,
	 * 						false if not.
	 */
	boolean relevantFor(INormedWorld world, IIndividual individual, IProperty property, IConcept concept);
	
	/**
	 * Checks if a given individual has to be substantiated for the fulfillment
	 * of the pursued norms.
	 * 
	 * @param world			normed world in which the relation subject to the specification resides
	 * @param individual	individual which is base of the check
	 * @return				true if individual has to be substantiated, false if not
	 */
	boolean relevantFor(INormedWorld world, IIndividual individual);
	
	/**
	 * Helper method to get the pursued Norms.
	 * 
	 * @return 	set of pursued norms.
	 */
	Set<INorm> getNorms();
}
 
