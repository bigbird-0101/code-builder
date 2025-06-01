package io.github.bigbird0101.code.core.config;

/**
 * @author Administrator
 */
public abstract class AbstractPropertyResolver implements ConfigurablePropertyResolver {
    @Override
    public String getProperty(String propertyKey) {
        return getProperty(propertyKey,String.class);
    }
}
