package io.github.bigbird0101.code.core.factory.config;

import io.github.bigbird0101.code.core.factory.ConfigurableTemplateFactory;

/**
 * init TemplateDefinition
 * @author bigbird-0101
 */
public interface TemplateFactoryPostProcessor {
    /**
     * post process
     *
     * @param configurableTemplateFactory configurableTemplateFactory
     */
    void postProcessTemplateFactory(ConfigurableTemplateFactory configurableTemplateFactory);
}
