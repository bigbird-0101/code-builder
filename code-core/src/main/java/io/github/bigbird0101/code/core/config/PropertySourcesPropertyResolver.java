package io.github.bigbird0101.code.core.config;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
        if (null != propertySourceTemplate.getSource() &&
                propertySourceTemplate.getSource() instanceof String propertySourceTemplateSource &&
                isJsonValid((String) propertySourceTemplate.getSource())
        ) {
            return Convert.convert(targetClass, JSON.parseObject(propertySourceTemplateSource));
        }
        return Convert.convert(targetClass, propertySourceTemplate.getSource());
    }

    public boolean isJsonValid(String jsonString) {
        try {
            JSONObject obj = JSON.parseObject(jsonString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 更新属性
     *
     * @param propertyKey 属性key
     * @param source 属性值
     * @return 是否更新成功
     */
    @Override
    public boolean updateProperty(String propertyKey, Object source) {
        return propertySources.updatePropertySource(propertyKey,source);
    }

    public PropertySources getPropertySources() {
        return propertySources;
    }
}
