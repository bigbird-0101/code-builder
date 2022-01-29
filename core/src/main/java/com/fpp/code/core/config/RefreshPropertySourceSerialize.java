package com.fpp.code.core.config;

/**
 * @author Administrator
 */
public interface RefreshPropertySourceSerialize {
    /**
     * 刷新一个propertySource到配置文件中
     * @param propertySources
     * @param <T>
     */
   <T> void refreshPropertySourceSerialize(PropertySource<T>... propertySources);
}
