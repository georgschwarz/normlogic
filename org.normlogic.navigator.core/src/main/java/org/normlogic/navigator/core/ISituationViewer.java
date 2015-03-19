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
 * Interface has to be implemented by a gui component which visualizes the
 * content of the a-box of the knowledge base, namely the individuals and its relations.
 */
public interface ISituationViewer {
    /**
     * The viewer has to add the given individual.
     * 
     * @param individual	individual which has to be visualized
     */
    public void add(IIndividual individual);
    
    /**
     * The viewer has to add an relation triple, containing the
     * relation between two individuals.
     * 
     * @param triple	the triple which has to be visualized
     */
    public void add(IAssertionTriple triple);
    
    /**
     * Notifies the viewer if the normed world is changed.
     * 
     * @param world		the new (changed) normed world.
     */
    public void changeNormedWorld(INormedWorld world);
}

