package com.fpp.code.core.config;

import java.util.Iterator;
import java.util.Properties;

public interface PropertySources {
    /**
     * 获取属性
     * @param name
     * @param <T>
     * @return
     */
    <T> PropertySource<T> getPropertySource(String name);

    /**
     * 修改属性
     * @param name
     * @param object
     * @param <T>
     * @return
     */
    <T> boolean updatePropertySource(String name,T object);

    /**
     * 添加属性
     * @param propertySource
     * @param <T>
     * @return
     */
    <T> boolean addPropertySource(PropertySource<T> propertySource);

    /**
     * 获取迭代器
     * @return
     */
    <T> Iterator<PropertySource<?>> iterator();

    default Properties convertProperties(PropertySources propertySources){
        Iterator<PropertySource<?>> iterable = propertySources.iterator();
        Properties properties=new Properties();
        while(iterable.hasNext()){
            PropertySource<Object> next = (PropertySource<Object>) iterable.next();
            String name = next.getName();
            Object source = next.getSource();
            properties.put(name,source);
        }
        return properties;
    }
}
