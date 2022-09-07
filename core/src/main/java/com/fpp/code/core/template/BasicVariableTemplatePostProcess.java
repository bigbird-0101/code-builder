package com.fpp.code.core.template;

import com.fpp.code.core.factory.config.TemplatePostProcessor;

/**
 * 模板中基础变量 {@link com.fpp.code.core.template.AbstractTemplate#templateVariables} 设置处理器
 * @author fpp
 */
public class BasicVariableTemplatePostProcess implements TemplatePostProcessor {
    @Override
    public Template postProcessAfterInstantiation(Template template, String templateName) {
        if(template instanceof AbstractTemplate){
            AbstractTemplate abstractTemplate= (AbstractTemplate) template;
            abstractTemplate.initTemplateVariables();
        }
        return template;
    }
}
