package com.fpp.code.core.factory.config;

import com.fpp.code.core.factory.AbstractOperateTemplateTemplateFactory;
import com.fpp.code.core.template.Template;

/**
 * template post processor
 * when BeforeInstantiation will invoke  com.fpp.code.core.factory.config.TemplatePostProcessor#postProcessBeforeInstantiation(com.fpp.code.core.template.Template, java.lang.String)
 * when AfterInstantiation will invoke com.fpp.code.core.factory.config.TemplatePostProcessor#postProcessAfterInstantiation(com.fpp.code.core.template.Template, java.lang.String)
 * @author fpp
 * @see AbstractOperateTemplateTemplateFactory
 */
public interface TemplatePostProcessor {
    /**
     *  when BeforeInstantiation will invoked
     * @param template template instant
     * @param templateName template name
     * @return
     */
    default Template postProcessBeforeInstantiation(Template template, String templateName){
        return template;
    }

    /**
     *  when AfterInstantiation will invoked
     * @param template template instant
     * @param templateName template name
     * @return
     */
    default Template postProcessAfterInstantiation(Template template, String templateName){
        return template;
    }
}
