package io.github.bigbird0101.code.core.cache;

import io.github.bigbird0101.code.core.cache.impl.SimpleCacheImpl;

import java.util.Set;

/**
 * 缓存接口
 * @see SimpleCacheImpl 默认实现
 * @see CacheKey 缓存key
 * @author fpp
 * @version 1.0
 */
public interface Cache<K,C>{
    /**
     * 缓存数据
     *
     * @param key    缓存key
     * @param object 缓存对象
     */
    void put(K key, C object);

    /**
     * 获取缓存数据
     * @param key 缓存key
     * @return 缓存对象
     */
    C get(K key);

    /**
     * 删除缓存数据
     * @param key 缓存key
     */
    void remove(K key);

    /**
     * 清空缓存数据
     */
    void clear();

    /**
     * 获取缓存数据key集合
     * @return 缓存数据key集合
     */
    Set<K> getKeys();
}
