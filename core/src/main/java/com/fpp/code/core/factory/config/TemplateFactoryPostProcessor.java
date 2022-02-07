package com.fpp.code.core.factory.config;

import com.fpp.code.core.factory.ConfigurableTemplateFactory;

/**
 * init TemplateDefinition
 */
public interface TemplateFactoryPostProcessor {
    void postProcessTemplateFactory(ConfigurableTemplateFactory configurableTemplateFactory);
}
