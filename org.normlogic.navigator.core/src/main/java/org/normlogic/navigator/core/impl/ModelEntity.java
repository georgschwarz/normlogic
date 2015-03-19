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


import org.normlogic.navigator.core.IModelEntity;

abstract public class ModelEntity implements IModelEntity {
	protected Object object;
	KnowledgeBase kb;
	protected ModelEntity(KnowledgeBase kb, Object object) {
		this.object = object;
		this.kb = kb;
	}
	
	public String getLabel() {
		return new String("unknown");
	}
	
	public KnowledgeBase getKnowledgeBase() {
		return kb;
	}
	
	public Object getObject() {
		return object;
	}

   @Override
    public int hashCode() {
	   int hash = 17;
	   hash = 5 * hash + ((object == null) ? 0 : object.hashCode());
	   hash = 5 * hash + ((kb == null) ? 0 : kb.hashCode());
       return (hash);
    }
	    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ModelEntity other = (ModelEntity)obj;
        if (object == null) {
            if (other.object != null) {
                return false;
            }
        }
        else if (!object.equals(other.object)) {
            return false;
        }
        return true;
    }
}
