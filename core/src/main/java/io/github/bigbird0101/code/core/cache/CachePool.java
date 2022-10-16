package io.github.bigbird0101.code.core.cache;

import io.github.bigbird0101.code.core.cache.impl.SimpleCacheImpl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * 缓存池
 * @author Administrator
 */
public class CachePool {
    private static List<Cache<?,?>> caches=new CopyOnWriteArrayList<>();
    public static <K, C> void register(Cache<K, C> cache){
        caches.add(cache);
    }
    public static <K, C> void clear(Cache<K, C> cache){
        caches.stream().filter(c-> caches.equals(c)&&cache.hashCode()==c.hashCode()).findFirst().ifPresent(Cache::clear);
    }

    public static <K, C> void clear(Predicate<? super Cache<?,?>> predicate){
        caches.stream().filter(predicate).forEach(Cache::clear);
    }

    public static <K, C> void clearAll(){
        caches.forEach(Cache::clear);
    }

    public static <K, C> Cache<K, C> build(int capacity){
        Cache<K, C> cache = new SimpleCacheImpl<>(capacity);
        register(cache);
        return cache;
    }
}
