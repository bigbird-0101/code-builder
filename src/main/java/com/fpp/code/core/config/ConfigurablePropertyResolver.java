package com.fpp.code.core.config;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/1/6 13:38
 */
public interface ConfigurablePropertyResolver extends PropertyResolver{
    /**
     * 更新属性
     * @param propertyKey
     * @param source
     * @return
     */
    boolean updateProperty(String propertyKey,Object source);

}
