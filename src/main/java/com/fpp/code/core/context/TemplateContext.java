package com.fpp.code.core.context;

import com.fpp.code.core.factory.ConfigurableListableTemplateFactory;
import com.fpp.code.core.factory.ConfigurableTemplateFactory;
import com.fpp.code.core.factory.config.EnvironmentCapable;

/**
 * @author Administrator
 */
public interface TemplateContext extends ConfigurableTemplateFactory, EnvironmentCapable {
    /**
     * 获取模板工厂
     *
     * @return
     */
    ConfigurableListableTemplateFactory getTemplateFactory();
}
