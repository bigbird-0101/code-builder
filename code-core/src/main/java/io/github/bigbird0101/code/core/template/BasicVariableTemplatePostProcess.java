package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.factory.config.TemplatePostProcessor;

/**
 * 模板中基础变量 设置处理器
 * @author fpp
 */
public class BasicVariableTemplatePostProcess implements TemplatePostProcessor {
    @Override
    public Template postProcessAfterInstantiation(Template template, String templateName) {
        return template;
    }
}
