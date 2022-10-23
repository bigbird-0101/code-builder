package io.github.bigbird0101.code.core.factory.config;

import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;

/**
 * 模板工厂
 * @author fpp
 */
public interface TemplateFactory {
    /**
     * 获取模板
     * @param templateName 模板名
     * @return 模板对象
     */
    Template getTemplate(String templateName);

    /**
     * 获取模板
     * @param templateName 模板名
     * @return 模板对象
     */
    MultipleTemplate getMultipleTemplate(String templateName);
}
