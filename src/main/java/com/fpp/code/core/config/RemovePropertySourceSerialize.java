package com.fpp.code.core.config;

/**
 * 删除一个配置并且序列化到文件当中
 */
public interface RemovePropertySourceSerialize {
    /**
     * 删除一个propertySource到配置文件中
     * @param propertySources
     * @param <T>
     */
    <T>  void removePropertySourceSerialize(PropertySource<T>... propertySources);
}
