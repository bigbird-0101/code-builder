package io.github.bigbird0101.code.core.factory.aware;

import io.github.bigbird0101.code.core.factory.config.TemplateFactory;

/**
 * 感知 templateFactory
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 19:27:36
 */
public interface TemplateFactoryAware {
    /**
     * 感知 templateFactory
     * @param templateFactory templateFactory
     */
    void setTemplateFactory(TemplateFactory templateFactory);
}
