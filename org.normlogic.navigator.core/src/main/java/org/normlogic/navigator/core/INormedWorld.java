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
	IHierarchyMaker<IConcept> getDomains();
}
