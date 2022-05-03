package com.fpp.code.core.cache.impl;

import cn.hutool.log.StaticLog;
import com.fpp.code.core.cache.Cache;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;

/**
 * 本地缓存实现 默认LRU
 * @author fpp
 */
public class CacheLocalLruImpl<K,C> implements Cache<K,C>, Serializable {
	private LinkedHashMap<K,C> cache;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CacheLocalLruImpl<?, ?> that = (CacheLocalLruImpl<?, ?>) o;
		return Objects.equals(cache, that.cache);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cache);
	}

	public CacheLocalLruImpl(int capacity){
		cache=new LinkedHashMap<K,C>(capacity,0.75f,true){
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<K, C> eldest) {
				return size()>capacity;
			}
		};
	}

	@Override
	public void put(K key, C object) {
		cache.put(key, object);
	}

	@Override
	public C get(K key) {
		return cache.get(key);
	}

	@Override
	public void remove(K key) {
		cache.remove(key);
	}

	@Override
	public void clear() {
		StaticLog.debug("clear {}",cache);
		cache.clear();
	}

	@Override
	public Set<K> getKeys() {
		return cache.keySet();
	}
}
