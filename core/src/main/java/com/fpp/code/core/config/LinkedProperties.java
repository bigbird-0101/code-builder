package com.fpp.code.core.config;

import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.function.BiConsumer;

/**
 * @author Administrator
 */
public class LinkedProperties extends Properties {
    private final LinkedHashMap<Object,Object> linkedHashMap=new LinkedHashMap<>();
    @Override
    public Object put(Object key, Object value) {
        return linkedHashMap.put(key, value);
    }

    @Override
    public void forEach(BiConsumer<? super Object, ? super Object> action) {
        linkedHashMap.forEach(action);
    }

    @Override
    public void clear() {
        linkedHashMap.clear();
    }

    @Override
    public boolean isEmpty() {
        return linkedHashMap.isEmpty();
    }

    @Override
    public int size() {
        return linkedHashMap.size();
    }

    @Override
    public Object get(Object key) {
        return linkedHashMap.get(key);
    }
}
