package com.fpp.code.core.context;

import com.fpp.code.core.context.aware.TemplateContextAware;
import com.fpp.code.core.factory.AbstractTemplateFactory;
import com.fpp.code.core.factory.ConfigurableListableTemplateFactory;
import com.fpp.code.core.factory.config.TemplatePostProcessor;

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
        if(templateFactory instanceof AbstractTemplateFactory){
            AbstractTemplateFactory abstractTemplateFactory= (AbstractTemplateFactory) templateFactory;
            for (TemplatePostProcessor postProcessor : abstractTemplateFactory.getTemplatePostProcessors()) {
                abstractTemplateFactory.invokeBaseAware(postProcessor);
                if(postProcessor instanceof TemplateContextAware){
                    TemplateContextAware templateContextAware= (TemplateContextAware) postProcessor;
                    templateContextAware.setTemplateContext(templateContext);
                }
            }
        }
    }
}
