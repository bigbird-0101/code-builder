package io.github.bigbird0101.code.core.config;

import cn.hutool.core.convert.Convert;

/**
 * @author fpp
 * @version 1.0
 * @since 2021/1/6 13:56
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
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String propertyKey, Class<T> targetClass) {
        T propertySource = (T) propertySources.getPropertySource(propertyKey);
        if(null==propertySource){
            return null;
        }
        final PropertySource<Object> propertySourceTemplate = propertySources.getPropertySource(propertyKey);
        return (T) Convert.convert(targetClass,propertySourceTemplate.getSource());
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
