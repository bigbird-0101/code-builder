package io.github.bigbird0101.code.core.context;

import io.github.bigbird0101.code.core.context.aware.TemplateContextAware;
import io.github.bigbird0101.code.core.factory.AbstractTemplateFactory;
import io.github.bigbird0101.code.core.factory.ConfigurableListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.config.TemplatePostProcessor;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 19:57:49
 */
public class TemplateContextAwareProcessor {
    private final TemplateContext templateContext;

    public TemplateContextAwareProcessor(TemplateContext templateContext) {
        this.templateContext = templateContext;
    }

    public void awareProcessor() {
        final ConfigurableListableTemplateFactory templateFactory = templateContext.getTemplateFactory();
        if (templateFactory instanceof AbstractTemplateFactory abstractTemplateFactory) {
            for (TemplatePostProcessor postProcessor : abstractTemplateFactory.getTemplatePostProcessors()) {
                abstractTemplateFactory.invokeBaseAware(postProcessor);
                if (postProcessor instanceof TemplateContextAware templateContextAware) {
                    templateContextAware.setTemplateContext(templateContext);
                }
            }
        }
    }
}
