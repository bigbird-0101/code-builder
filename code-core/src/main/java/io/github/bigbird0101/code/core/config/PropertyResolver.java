package io.github.bigbird0101.code.core.config;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author fpp
 * @version 1.0
 */
public interface PropertyResolver extends Serializable {
    /**
     * 获取属性值
     * @param propertyKey 属性key
     * @return 属性值
     */
    String getProperty(String propertyKey);

    /**
     * 获取属性值
     * @param propertyKey 属性key
     * @param targetClass 属性值Class
     * @return 属性值
     */
    <T> T getProperty(String propertyKey, Class<T> targetClass);

    /**
     * 获取属性值
     *
     * @param propertyKey  属性key
     * @param targetClass  属性值Class
     * @param defaultValue 默认值
     * @return 属性值
     */
    default <T> T getPropertyOrDefault(String propertyKey, Class<T> targetClass, T defaultValue) {
        return Optional.ofNullable(getProperty(propertyKey, targetClass)).orElse(defaultValue);
    }
    /**
     * 如果没有获取到配置就取默认值
     * @param propertyKey 属性key
     * @param defaultValue 默认值
     * @return 属性值
     */
    default String getPropertyOrDefault(String propertyKey,String defaultValue){
        return Optional.ofNullable(getProperty(propertyKey)).orElse(defaultValue);
    }
}
