package io.github.bigbird0101.code.core.config;

/**
 * @author Administrator
 */
public abstract class AbstractPropertyResolver implements ConfigurablePropertyResolver {
    @Override
    public String getProperty(String propertyKey) {
        return getProperty(propertyKey,String.class);
    }

    @Override
    public <T> T getProperty(String propertyKey, Class<T> targetClass) {
        return (T) getProperty(propertyKey,targetClass);
    }
}
