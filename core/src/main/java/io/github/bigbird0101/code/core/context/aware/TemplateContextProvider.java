package io.github.bigbird0101.code.core.context.aware;

import io.github.bigbird0101.code.core.context.TemplateContext;
import io.github.bigbird0101.code.core.template.AbstractTemplate;
import io.github.bigbird0101.code.core.template.AbstractTemplateResolver;
import io.github.bigbird0101.code.core.template.TemplateLangResolver;
import io.github.bigbird0101.code.core.template.TemplateTraceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author fpp
 * @version 1.0
 */
public abstract class TemplateContextProvider {
    private Logger logger= LogManager.getLogger(TemplateContextProvider.class);

    private static TemplateContext templateContext;

    public static void setTemplateContext(TemplateContext templateContext) {
        TemplateContextProvider.templateContext = templateContext;
    }

    public static void doPushEventTemplateContextAware() {
        AbstractTemplate currentTemplate = (AbstractTemplate) TemplateTraceContext.getCurrentTemplate();
        AbstractTemplateResolver templateResolver = (AbstractTemplateResolver) currentTemplate.getTemplateResolver();
        List<TemplateLangResolver> templateLangResolverList = templateResolver.getTemplateLangResolverList();
        for(TemplateLangResolver templateLangResolver:templateLangResolverList){
            if(templateLangResolver instanceof TemplateContextAware){
                TemplateContextAware aware= (TemplateContextAware) templateLangResolver;
                aware.setTemplateContext(templateContext);
            }
        }
    }

    public TemplateContext getTemplateContext() {
        return templateContext;
    }
}