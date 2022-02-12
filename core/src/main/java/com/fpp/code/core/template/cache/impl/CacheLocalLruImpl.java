package com.fpp.code.core.template.cache.impl;

import com.fpp.code.core.template.cache.Cache;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * 本地缓存实现 默认LRU
 * @author fpp
 */
public class CacheLocalLruImpl implements Cache, Serializable {
	private LinkedHashMap<Object,Object> cache;
	
	public CacheLocalLruImpl(int capacity){
		cache=new LinkedHashMap<Object,Object>(capacity,0.75f,true){
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<Object, Object> eldest) {
				return size()>capacity;
			}
		};
	}

	@Override
	public void put(Object key, Object object) {
		cache.put(key, object);
	}

	@Override
	public Object get(Object key) {
		return cache.get(key);
	}

	@Override
	public void remove(Object key) {
		cache.remove(key);
	}

	@Override
	public void clear() {
		cache.clear();
	}
}
