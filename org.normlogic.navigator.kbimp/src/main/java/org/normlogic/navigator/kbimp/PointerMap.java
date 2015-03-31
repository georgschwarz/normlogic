package org.normlogic.navigator.kbimp;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.normlogic.navigator.core.impl.KnowledgeBase;

class PointerMap<K, S extends K, V> {
	Map<K, V> map = new HashMap<>();
	Map<V, K> mapReverse = new HashMap<>();
 	KnowledgeBaseOwlapi kb;
	Class<S> implementationClass;
	PointerMap(KnowledgeBaseOwlapi kb, Class<S> implementationClass) {
		this.kb = kb;
		this.implementationClass = implementationClass;
	}
	K createFrom(V owlObject) {
		try {
			// make sure that we have a 1:1 relation
			if (mapReverse.containsKey(owlObject)) {
				return mapReverse.get(owlObject);
			}
    		S implementation;
			implementation = implementationClass.getConstructor(KnowledgeBase.class, Object.class).newInstance(kb, owlObject);
    		map.put(implementation, owlObject);
    		mapReverse.put(owlObject, implementation);
    		return implementation;     		
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	V wrap(K pointer) {
		return map.get(pointer);
	}
	K wrapFromValue(V value) {
		return mapReverse.get(value);
	}
	void delete(K key) {
		if (key != null) {
			deleteFromValue(map.remove(key));
		}
	}
	void deleteFromValue(V value) {
		if (value != null) {
			delete(mapReverse.remove(value));
		}
	}
}
