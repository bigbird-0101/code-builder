package com.fpp.code.core.context.aware;

import com.fpp.code.core.context.TemplateContext;
import com.fpp.code.core.template.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/1/8 9:35
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