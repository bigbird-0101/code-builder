package com.fpp.code.core.config;

import java.io.Serializable;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/5/7 15:53
 */
public interface PropertyResolver extends Serializable {
    /**
     * 获取属性值
     * @param propertyKey
     * @return
     */
    String getProperty(String propertyKey);

    /**
     * 获取属性值
     * @param propertyKey
     * @param targetClass
     * @return
     */
    <T> T getProperty(String propertyKey, Class<T> targetClass);
}
