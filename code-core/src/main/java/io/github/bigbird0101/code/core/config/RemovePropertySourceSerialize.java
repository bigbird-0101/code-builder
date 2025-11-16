package io.github.bigbird0101.code.core.config;

/**
 * 删除一个配置并且序列化到文件当中
 * @author bigbird0101
 */
public interface RemovePropertySourceSerialize {
    /**
     * 删除一个propertySource到配置文件中
     * @param propertySources propertySources
     * @param <T>  T
     */
    @SuppressWarnings("unchecked")
    <T>  void removePropertySourceSerialize(PropertySource<T>... propertySources);
}
