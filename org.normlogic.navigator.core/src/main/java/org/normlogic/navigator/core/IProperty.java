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

/**
 * A property represents a possible relation between two concepts. In the
 * context of an individual assertion it is the type of a relation between
 * two individuals.
 * 
 * @author Georg Schwarz
 *
 */
public interface IProperty {
	/**
	 * Gives a human readable name of the relation.
	 * 
	 * @return human readable name as a String
	 */
	String getLabel();
}
