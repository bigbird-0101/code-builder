package io.github.bigbird0101.code.core.context.aware;

import io.github.bigbird0101.code.core.context.TemplateContext;
import io.github.bigbird0101.code.core.template.AbstractAbstractTemplateResolver;
import io.github.bigbird0101.code.core.template.AbstractTemplate;
import io.github.bigbird0101.code.core.template.TemplateLangResolver;
import io.github.bigbird0101.code.core.template.TemplateTraceContext;

import java.util.List;

/**
 * @author fpp
 * @version 1.0
 */
public abstract class AbstractTemplateContextProvider {
    private static TemplateContext templateContext;

    public static void setTemplateContext(TemplateContext templateContext) {
        AbstractTemplateContextProvider.templateContext = templateContext;
    }

    public static void doPushEventTemplateContextAware() {
        AbstractTemplate currentTemplate = (AbstractTemplate) TemplateTraceContext.getCurrentTemplate();
        if(null!=currentTemplate) {
            AbstractAbstractTemplateResolver templateResolver = (AbstractAbstractTemplateResolver) currentTemplate.getTemplateResolver();
            List<TemplateLangResolver> templateLangResolverList = templateResolver.getTemplateLangResolverList();
            for (TemplateLangResolver templateLangResolver : templateLangResolverList) {
                if (templateLangResolver instanceof TemplateContextAware) {
                    TemplateContextAware aware = (TemplateContextAware) templateLangResolver;
                    aware.setTemplateContext(templateContext);
                }
            }
        }
    }

    public TemplateContext getTemplateContext() {
        return templateContext;
    }
}