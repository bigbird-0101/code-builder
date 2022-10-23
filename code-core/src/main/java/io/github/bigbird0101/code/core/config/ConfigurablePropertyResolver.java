package io.github.bigbird0101.code.core.config;

/**
 * @author fpp
 * @version 1.0
 */
public interface ConfigurablePropertyResolver extends PropertyResolver{
    /**
     * 更新属性
     * @param propertyKey 属性key
     * @param source 属性对象
     * @return 是否设置成功 true-成功 false-失败
     */
    boolean updateProperty(String propertyKey, Object source);

}
