package io.github.bigbird0101.code.core.config;

import java.io.Serializable;
import java.util.Optional;

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

    /**
     * 如果没有获取到配置就取默认值
     * @param propertyKey 属性key
     * @param defaultValue 默认值
     * @return
     */
    default String getPropertyOrDefault(String propertyKey,String defaultValue){
        return Optional.ofNullable(getProperty(propertyKey)).orElse(defaultValue);
    }
}
