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
 * Base class of the entity of the knowledge model, including the concept, 
 * individual and assertion types.
 * 
 * @author Georg Schwarz
 *
 */
public interface IModelEntity {
	/**
	 * Gives a human readable name of the entity.
	 * 
	 * @return	human readable name of the entity
	 */
	String getLabel();
}
