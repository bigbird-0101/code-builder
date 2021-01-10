package com.fpp.code.core.config;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author fpp
 */
public class MultiplePropertySources implements PropertySources {
    private final List<PropertySource<?>> sources=new CopyOnWriteArrayList<>();
    @Override
    public PropertySource<?> getPropertySource(String name) {
        for (PropertySource<?> propertySource : this.sources) {
            if (propertySource.getName().equals(name)) {
                return propertySource;
            }
        }
        return null;
    }

    @Override
    public <T> boolean updatePropertySource(String name,T object) {
        removeIfPresent(getPropertySource(name));
        return this.sources.add(new GeneratePropertySource(name,object));
    }

    @Override
    public <T> boolean addPropertySource(PropertySource<T> propertySource) {
        return updatePropertySource(propertySource.getName(),propertySource.getSource());
    }

    @Override
    public <T> Iterator<PropertySource<?>> iterator() {
        return sources.iterator();
    }

    public void addLast(PropertySource<?> propertySource){
        removeIfPresent(propertySource);
        this.sources.add(propertySource);
    }

    public void removeIfPresent(PropertySource<?> propertySource){
        this.sources.remove(propertySource);
    }

    @Override
    public String toString() {
        return "MultiplePropertySources{" +
                "sources=" + sources +
                '}';
    }
}
