package com.fpp.code.config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MutablePropertySources implements PropertySources {
    private final List<PropertySource<?>> sources=new CopyOnWriteArrayList<>();
    @Override
    public PropertySource<?> getPropertySource(String name) {
        int index=this.sources.indexOf(PropertySource.named(name));
        return  (index != -1 ? this.sources.get(index) : null);
    }

    public void addLast(PropertySource<?> propertySource){
        removeIfPresent(propertySource);
        this.sources.add(propertySource);
    }

    public void removeIfPresent(PropertySource<?> propertySource){
        this.sources.remove(propertySource);
    }
}
