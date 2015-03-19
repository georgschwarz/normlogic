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
 * Interface to interactively get the name of a created entity.
 * 
 * @author Georg Schwarz
 *
 */
public interface IAddEntityDialog {
	/**
	 * Run the implemented dialog.
	 * 
	 * @param initialValue	preset name value
	 * @return				the new name of the entity, may be null, if no 
	 * 						name is given.					 
	 */
	String runDialog(final String initialValue);
}
