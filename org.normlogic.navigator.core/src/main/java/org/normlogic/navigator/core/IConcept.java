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
/**
 * Normlogic, Copyright (C) 2015  Georg Schwarz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.normlogic.navigator.core;

import java.util.Set;

/** 
 * This type represents a (named) concept of the underlying description logic of
 * the used knowledge base. It is always created by the knowledge base and has only
 * elemental methods. 
 * 
 * @author Georg Schwarz
 *
 */
public interface IConcept extends IModelEntity, Comparable<IConcept> {
	/**
	 * Returns all subconcepts in the sense of an inclusion, so if C is as subconcept
	 * of D, every individual of type C has also type D. The subconcepts returned should
	 * be identified by reasoning.
	 * 
	 * @param direct	flag to retrieve only direct subconcepts.
	 * @return			a set of subconcepts
	 */
	Set<IConcept> getSubConcepts(boolean direct);
	
	/**
	 * Creates an individual out of the given concept, named interactively by the given
	 * dialog interface. The individual is not asserted to the concept.
	 * 
	 * @param dialog	the dialog interface given to name the individual
	 * @return			the created individual
	 */
	IIndividual asIndividual(IAddEntityDialog dialog);
	
	/**
	 * Creates an individual out of the given concept, named interactively by the given
	 * dialog interface. The individual is asserted to the concept.
	 * 
	 * @param dialog	the dialog interface given to name the individual
	 * @return			the created individual
	 */
	IIndividual createAssertedIndividual(IAddEntityDialog dialog);
	
	/**
	 * Returns all norms of the given normed world, in which the concept is qualified 
	 * by an obligation.
	 * 
	 * @param currentWorld	the normed world, in which the concept resides
	 * @return				the set of norms, in which the concept is qualified by an obligation			
	 */
	Set<INorm> getObligationNorms(INormedWorld currentWorld);
}
