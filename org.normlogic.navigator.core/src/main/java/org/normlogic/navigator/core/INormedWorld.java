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
 * A normed world is an universe of possible concepts and relations, which 
 * are relevant to a set of norms.
 *  
 * @author Georg Schwarz
 *
 */
public interface INormedWorld {
	/**
	 * Gives all concepts, which are part of the normed world and are defined
	 * as a domain in the included relations.
	 *  
	 * @return set of domain concepts
	 */
	IHierarchy<IConcept> getDomains();
	
	/**
	 * Check if Concept is part of the normed world.
	 * 
	 * @param concept to be checked
	 * @return true if concept is part of the normed world, false if not.
	 */
	boolean contains(final IConcept concept);
	
	/**
	 * Returns those concepts, which are part of the normed world.
	 * 
	 * @param concepts set of concepts, which will be filtered
	 * @return set of concepts found in the normed world
	 */
	IHierarchy<IConcept> retainIncluded(final Set<IConcept> concepts);
}
