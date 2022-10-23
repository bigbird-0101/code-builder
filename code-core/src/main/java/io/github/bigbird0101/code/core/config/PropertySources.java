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
     * @param name 属性key
     * @param <T> 属性值对象泛型
     * @return 属性对象
     */
    <T> PropertySource<T> getPropertySource(String name);

    /**
     * 修改属性
     * @param name 属性key
     * @param object 属性值
     * @param <T> 属性值对象泛型
     * @return 是否修改成功 true-成功 false-失败
     */
    <T> boolean updatePropertySource(String name, T object);

    /**
     * 删除属性
     * @param propertySource 属性对象
     */
    void removeIfPresent(PropertySource<?> propertySource);

    /**
     * 添加属性
     * @param propertySource 属性对象
     * @param <T> 属性值对象泛型
     * @return 是否添加成功 true-成功 false-失败
     */
    <T> boolean addPropertySource(PropertySource<T> propertySource);

    /**
     * 获取迭代器
     * @return 迭代器
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
