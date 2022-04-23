package com.fpp.code.core.cache;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.fpp.code.core.cache.impl.CacheLocalLruImpl;

import java.util.Set;
import java.util.function.Predicate;

/**
 * 缓存池
 * @author Administrator
 */
public class CachePool {
    private static Set<Cache<?,?>> caches=new ConcurrentHashSet<>();
    public static <K, C> void register(Cache<K, C> cache){
        caches.add(cache);
    }
    public static <K, C> void clear(Cache<K, C> cache){
        caches.stream().filter(c-> caches.equals(c)).findFirst().ifPresent(Cache::clear);
    }

    public static <K, C> void clear(Predicate<? extends Cache<?, ?>> predicate){
        caches.stream().filter((Predicate<? super Cache<?, ?>>) predicate).findFirst().ifPresent(Cache::clear);
    }

    public static <K, C> void clearAll(){
        caches.forEach(Cache::clear);
    }

    public static <K, C> Cache<K, C> build(int capacity){
        Cache<K, C> cache = new CacheLocalLruImpl<>(capacity);
        register(cache);
        return cache;
    }
}
