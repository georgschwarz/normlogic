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

public interface Event {
	public static final String ONTOLOGY_LOADED = "NORMLOGIC/NAVIGATE/ONTOLOGY_LOADED";
	public static final String ONTOLOGY_CHANGED = "NORMLOGIC/NAVIGATE/ONTOLOGY_CHANGED";
	public static final String NORMED_WORLD_CHANGED = "NORMLOGIC/NAVIGATE/NORMED_WORLD_CHANGED";
	public static final String SHOW_NORM = "NORMLOGIC/NAVIGATE/SHOW_NORM";
	public static final String PURSUE_NORM = "NORMLOGIC/NAVIGATE/PURSUE_NORM";
}
