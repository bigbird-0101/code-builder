package io.github.bigbird0101.code.core.config;

/**
 * @author fpp
 * @version 1.0
 */
public interface ConfigurablePropertyResolver extends PropertyResolver{
    /**
     * 更新属性
     * @param propertyKey
     * @param source
     * @return
     */
    boolean updateProperty(String propertyKey, Object source);

}
