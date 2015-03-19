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

import org.normlogic.navigator.core.IConcept;
import org.normlogic.navigator.core.IProperty;


public class WorldTriple {
	
	IConcept domain;
	IProperty property;
	IConcept range;
	
	public WorldTriple(IConcept domain, IProperty property, IConcept range) {
		this.domain = domain;
		this.property = property;
		this.range = range;
	}
   @Override
    public int hashCode() {
        int hash = 31;
        hash = 5 * hash + ((domain == null) ? 0 : domain.hashCode());
        hash = 5 * hash + ((property == null) ? 0 : property.hashCode());
        hash = 5 * hash + ((range == null) ? 0 : domain.hashCode());
        return hash;
    }
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        WorldTriple other = (WorldTriple)obj;
        if (domain == null && other.domain != null) return false;
        if (property == null && other.property != null) return false;
        if (range == null && other.range != null) return false;
        if (!domain.equals(other.domain)) return false;
        if (!property.equals(other.property)) return false;
        if (!range.equals(other.range)) return false;
        return true;
    }
}
