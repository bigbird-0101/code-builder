package com.fpp.code.core.template.cache;

/**
 * 缓存接口
 * @see com.fpp.code.core.template.cache.impl.CacheLocalLruImpl 默认实现
 * @see CacheKey 缓存key
 * @author fpp
 * @version 1.0
 * @date 2020/10/20 14:35
 */
public interface Cache {
    void put(Object key,Object object);
    Object get(Object key);
    void remove(Object key);
    void clear();
}
