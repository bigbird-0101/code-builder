package io.github.bigbird0101.code.core.factory.config;

import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;

/**
 * 模板注册
 * @author fpp
 */
public interface TemplateRegistry {
    /**
     * 注册模板
     * @param templateName 模板名
     * @param template 模板对象
     */
    void registerTemplate(String templateName, Template template);

    /**
     * 获取模板
     * @param templateName 模板名
     * @return 模板
     */
    Template getSingletonTemplate(String templateName);

    /**
     * 注册组合模板
     * @param templateName 模板名
     * @param template 模板
     */
    void registerMultipleTemplate(String templateName, MultipleTemplate template);

    /**
     * 获取组合模板
     * @param templateName 模板名
     * @return 组合模板
     */
    MultipleTemplate getSingletonMultipleTemplate(String templateName);

    /**
     * 替换同名的组合模板
     * @param multipleTemplate  组合模板
     * @return 替换同名后的组合模板
     */
    MultipleTemplate replaceMultipleTemplate(MultipleTemplate multipleTemplate);

    /**
     * 替换同名的模板
     * @param template 模板
     * @return 替换同名后的模板
     */
    Template replaceTemplate(Template template);
}
