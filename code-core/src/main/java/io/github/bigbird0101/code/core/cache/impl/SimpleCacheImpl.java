package io.github.bigbird0101.code.core.cache.impl;

import cn.hutool.log.StaticLog;
import io.github.bigbird0101.code.core.cache.Cache;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存实现 默认LRU
 * @author fpp
 */
public class SimpleCacheImpl<K,C> implements Cache<K,C>, Serializable {
	private final Map<K,C> cache;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SimpleCacheImpl<?, ?> that = (SimpleCacheImpl<?, ?>) o;
		return Objects.equals(cache, that.cache);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cache);
	}

	public SimpleCacheImpl(int capacity){
		cache=new ConcurrentHashMap<>();
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
