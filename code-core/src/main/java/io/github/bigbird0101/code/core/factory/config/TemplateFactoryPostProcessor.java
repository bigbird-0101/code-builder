package io.github.bigbird0101.code.core.factory.config;

import io.github.bigbird0101.code.core.factory.ConfigurableTemplateFactory;

/**
 * init TemplateDefinition
 */
public interface TemplateFactoryPostProcessor {
    void postProcessTemplateFactory(ConfigurableTemplateFactory configurableTemplateFactory);
}
