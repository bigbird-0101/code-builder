package io.github.bigbird0101.code.core.config;

/**
 * @author Administrator
 */
public interface RefreshPropertySourceSerialize {
    /**
     * 刷新一个propertySource到配置文件中
     * @param propertySources propertySources
     * @param <T> 属性对象
     */
    @SuppressWarnings("unchecked")
   <T> void refreshPropertySourceSerialize(PropertySource<T>... propertySources);
}
