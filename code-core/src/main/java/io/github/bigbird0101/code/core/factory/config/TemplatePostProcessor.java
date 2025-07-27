package io.github.bigbird0101.code.core.factory.config;

import io.github.bigbird0101.code.core.factory.AbstractOperateTemplateTemplateFactory;
import io.github.bigbird0101.code.core.template.Template;

/**
 * template post processor
 * when BeforeInstantiation will invoke  io.github.bigbird0101.code.core.factory.config.TemplatePostProcessor#postProcessBeforeInstantiation(io.github.bigbird0101.code.core.template.Template, java.lang.String)
 * when AfterInstantiation will invoke io.github.bigbird0101.code.core.factory.config.TemplatePostProcessor#postProcessAfterInstantiation(io.github.bigbird0101.code.core.template.Template, java.lang.String)
 * @author fpp
 * @see AbstractOperateTemplateTemplateFactory
 */
public interface TemplatePostProcessor {
    /**
     *  when BeforeInstantiation will invoked
     * @param template template instant
     * @param templateName template name
     * @return template
     */
    default Template postProcessBeforeInstantiation(Template template, String templateName){
        return template;
    }

    /**
     *  when AfterInstantiation will invoked
     * @param template template instant
     * @param templateName template name
     * @return template
     */
    default Template postProcessAfterInstantiation(Template template, String templateName){
        return template;
    }
}
