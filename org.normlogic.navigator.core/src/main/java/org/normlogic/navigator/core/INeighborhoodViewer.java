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
 * Interfaced object to view the content of the a-box of the knowledge in respect
 * to a defined individual.
 * 
 * @author Georg Schwarz
 *
 */
public interface INeighborhoodViewer {
	/**
	 * Renders all possible triple relations in respect to an individual.
	 * 
	 * @param property	the property of the triple
	 * @param concepts	the range of the triple
	 */
	void add(IProperty property, IHierarchy<IConcept> concepts);
}
