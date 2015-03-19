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
package org.normlogic.navigator.core.impl;

import java.util.Set;

import org.normlogic.navigator.core.INorm;
import org.normlogic.navigator.core.IOntology;

public class Ontology extends ModelEntity implements IOntology {

	Object ontology;	
	
	public Ontology(KnowledgeBase kb, Object ontology) {
		super(kb, ontology);
		this.ontology = ontology;
	}

	@Override
	public String getLabel() {
		return kb.getLabelOf(this);
	}
	
	@Override
	public Set<INorm> getNorms() {
		return kb.getNorms(this);
	}
}
