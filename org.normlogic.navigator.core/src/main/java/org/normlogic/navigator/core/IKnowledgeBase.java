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
 * The core object of the knowledge base. A knowledge base consists of an 
 * initial empty ontology as a root, in which the a-box assertion will be inserted. Into
 * this root ontology loaded ontolgies will be included, for example as an import in respect to
 * a possible OWL-ontology.
 * 
 * @author Georg Schwarz
 *
 */
public interface IKnowledgeBase {
	/**
	 * Visualize the a-box of the root ontology using the given viewer object.
	 * 	
	 * @param viewer	the viewer object, in which the a-box is visualized
	 */
	void visualize(ISituationViewer viewer); 
	
	/**
	 * Loads respectively includes an ontology into the knowledgebase.
	 *  
	 * @param fileName	the path and name of the file.
	 * @return			the included ontology object.
	 * @throws Exception
	 */
	IOntology load(final String fileName) throws Exception;
	void addChangeListener(final IKnowledgeBaseChangeListener listener);
	INormedWorld getNormedWorld() throws Exception;
	Set<IOntology> getLoadedOntologies();
}
