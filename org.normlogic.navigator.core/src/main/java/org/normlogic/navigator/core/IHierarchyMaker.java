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
