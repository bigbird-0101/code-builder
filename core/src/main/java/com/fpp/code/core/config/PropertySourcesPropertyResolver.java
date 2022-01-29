package com.fpp.code.core.config;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/1/6 13:56
 */
public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {

    private final PropertySources propertySources;

    protected PropertySourcesPropertyResolver(PropertySources propertySources) {
        this.propertySources=propertySources;
    }

    @Override
    public String getProperty(String propertyKey) {
        return getProperty(propertyKey,String.class);
    }

    @Override
    public <T> T getProperty(String propertyKey, Class<T> targetClass) {
        T propertySource = (T) propertySources.getPropertySource(propertyKey);
        if(null==propertySource){
            return null;
        }
        return (T)propertySources.getPropertySource(propertyKey).getSource();
    }

    /**
     * 更新属性
     *
     * @param propertyKey
     * @param source
     * @return
     */
    @Override
    public boolean updateProperty(String propertyKey, Object source) {
        return propertySources.updatePropertySource(propertyKey,source);
    }

    public PropertySources getPropertySources() {
        return propertySources;
    }
}
