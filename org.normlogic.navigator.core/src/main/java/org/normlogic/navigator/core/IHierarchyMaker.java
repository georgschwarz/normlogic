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
 * Interface to a tool, which handles hierachical structures, like
 * concept/subconcept or property/subconcept hierarchies.
 * 
 * @author Georg Schwarz
 *
 * @param <T>
 */
public interface IHierarchyMaker<T> {
	/**
	 * Finds the top level entities of the given hierarchy.
	 * 
	 * @return the top level entities.
	 */
	Set<T> getTopLevelEntities();
	
	/**
	 * Finds the direct child entites of the given entity in the given
	 * hierarchy.
	 * 
	 * @param entity	entity, for whom the child entities have to be found.
	 * @return			a set of child entities.
	 */
	Set<T> getDirectChildEntitesOf(T entity);
}
