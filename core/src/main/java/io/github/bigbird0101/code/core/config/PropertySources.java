package io.github.bigbird0101.code.core.config;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author Administrator
 */
public interface PropertySources extends Serializable {
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
    <T> boolean updatePropertySource(String name, T object);

    /**
     * 删除属性
     * @param propertySource
     */
    void removeIfPresent(PropertySource<?> propertySource);

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

    /**
     * convert Properties
     * @return Properties
     */
    default Properties convertProperties(){
        Iterator<PropertySource<?>> iterable = this.iterator();
        Properties properties=new LinkedProperties();
        while(iterable.hasNext()){
            PropertySource<Object> next = (PropertySource<Object>) iterable.next();
            String name = next.getName();
            Object source = next.getSource();
            properties.put(name,source);
        }
        return properties;
    }
}
