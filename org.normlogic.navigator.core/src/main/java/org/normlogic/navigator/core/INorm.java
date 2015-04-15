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


/**
 * A Norm is the core entity of normlogic. It is the reification of an entity
 * which qualify something in the sense of what should be. A qualification is for 
 * example an obligation. This qualification is subject to a condition. Therefore 
 * every Norm has an associated condition, a typed situation, if fulfilled, something
 * is qualified.
 * 
 * In the normlogic project a norm has currently two associations. A context association,
 * this means a association to a typed situation, which represents the condition of the norm. 
 * And one optional typed situation which represents the qualification, for example a typed 
 * situation which is obliged.
 * 
 * @author Georg Schwarz
 *
 */
public interface INorm extends IModelEntity, Comparable<INorm> {
	/**
	 * Returns the human readable text, the content of the norm.
	 * @return	the text as a string.
	 */
	String getText();
	
	/**
	 * Checks if the given individual is fulfilled by the norm. A Norm is fulfilled
	 * if the individual meets its associated condition and also meets its associated
	 * conclusion.  
	 * 
	 * @param individual	individual to check
	 * @return				true if norm is fulfilled for the given individual,
	 * 						false if not
	 */
	boolean isFulfilledFor(IIndividual individual);
	
	/**
	 * Checks if the norm is fulfilled for at least on of the individuals
	 * included in the current knowledge base. A Norm is fulfilled
	 * if the individual meets its associated condition and also meets its associated
	 * conclusion.  
	 * 
	 * @return	true if norm is fulfilled, false if not
	 */
	boolean isFulfilled();
	
	/**
	 * Checks if the norm is not fulfilled but has to be fulfilled for one 
	 * of the individuals included in the current knowledge base. A norm
	 * has to be fulfilled subject to an individual in case the individual
	 * fulfills the condition of the norm but don´t meets its conclusion.
	 * 
	 * @return	true if norm has to be fulfilled, false if not.
	 */
	boolean hasToBeFulfilled();
	
	/**
	 * Checks if the norm has to be fulfilled for the given individual. A
	 * norm has to be fulfilled for the given individual in case the individual
	 * fulfills the condition of the norm but don´t meets its conclusion.
	 * 
	 * @param individual	the individual, the norm is checked for.
	 * @return				true if norm has to be fulfilled, false if not.
	 */
	boolean hasToBeFulfilledFor(IIndividual individual);
	
	String getUrl();

	String getRepresentationCondition();
	String getRepresentationConclusion();
}
