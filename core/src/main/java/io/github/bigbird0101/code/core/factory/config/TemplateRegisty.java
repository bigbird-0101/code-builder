package io.github.bigbird0101.code.core.factory.config;

import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;

/**
 * 模板注册
 * @author fpp
 */
public interface TemplateRegisty {
    /**
     * 注册模板
     * @param templateName
     * @param template
     */
    void registerTemplate(String templateName, Template template);

    /**
     * 获取模板
     * @param templateName
     * @return
     * @throws CodeConfigException
     */
    Template getSingletonTemplate(String templateName) throws CodeConfigException;

    /**
     * 注册组合模板
     * @param templateName
     * @param template
     */
    void registerMultipleTemplate(String templateName, MultipleTemplate template);

    /**
     * 获取组合模板
     * @param templateName
     * @return
     * @throws CodeConfigException
     */
    MultipleTemplate getSingletonMultipleTemplate(String templateName);

    /**
     * 替换同名的组合模板
     * @param multipleTemplate  组合模板
     */
    MultipleTemplate replaceMultipleTemplate(MultipleTemplate multipleTemplate);

    /**
     * 替换同名的模板
     * @param template 模板
     */
    Template replaceTemplate(Template template);
}
