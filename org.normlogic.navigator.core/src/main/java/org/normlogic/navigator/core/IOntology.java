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
 * An ontology is a loadable part of the knowledge base. It includes a formal
 * defined ontology and the normative definitions.
 * 
 * In normlogic the ontology represents a defined set of norms, for example all
 * normative clauses of a contract. 
 * 
 * @author Georg Schwarz
 *
 */
public interface IOntology {
	/**
	 * Get a human readable name of the (loaded) ontology.
	 * 
	 * @return	human readable name as a string.
	 */
	String getLabel();
	
	/**
	 * Get all norms included in the ontology.
	 * 
	 * @return	set of norms.
	 */
	Set<INorm> getNorms(); 
}
