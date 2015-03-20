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
 * This interface has to be implemented by an object which wants to
 * get informed in case of changes in the knowledge base. 
 * 
 * @author Georg Schwarz
 *
 */
public interface IKnowledgeBaseChangeListener {
	/**
	 * called every time the knowledge base is changed.
	 */
	public void knowledgeBaseChanged();
}
