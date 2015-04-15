package org.normlogic.navigator.core.impl;

import org.normlogic.navigator.core.IExpression;
import org.normlogic.navigator.core.INorm;

public class NormContext {

	public enum Type {
		condition, obligation
	} 
	
	public static Type CONDITION = Type.condition;
	public static Type OBLIGATION = Type.obligation;
	
	IExpression expression;
	
	Type type;
	INorm norm;
	
	public NormContext(INorm norm, Type type) {
		this.type = type;
		this.norm = norm;
	}
	
   @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + ((type == null) ? 0 : type.hashCode());
        hash = 17 * hash + ((norm == null) ? 0 : norm.hashCode());
        return hash;
    }
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NormContext other = (NormContext)obj;
        if (type == null && other.type != null) return false;
        if (norm == null && other.norm != null) return false;
        if (!type.equals(other.type)) return false;
        if (!norm.equals(other.norm)) return false;
        return true;
    }
}