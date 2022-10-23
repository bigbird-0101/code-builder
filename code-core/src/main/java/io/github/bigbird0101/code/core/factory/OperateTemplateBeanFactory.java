package io.github.bigbird0101.code.core.factory;

import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplateFactory;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;

/**
 * @author bigbird-0101
 */
public interface OperateTemplateBeanFactory extends TemplateFactory {
    /**
     * 创建模板
     * @param templateName 模板名
     * @param templateDefinition 模板定义
     * @return 模板对象
     */
    Template createTemplate(String templateName, TemplateDefinition templateDefinition);

    /**
     * 刷新模板 从配置文件中重新刷新模板
     * @param templateName 模板名
     */
    void refreshTemplate(String templateName);

    /**
     * 刷新容器中模板 将容器中的模板 并将模板写入到配置文件当中
     * @param template 模板对象
     * @return 刷新后的模板对象
     */
    Template refreshTemplate(Template template);
    /**
     * 删除模板
     * @param templateName 模板名
     */
    void removeTemplate(String templateName);

    /**
     * 刷新容器中组合模板 将容器中的模板 从配置文件中重新刷新模板
     * @param multipleTemplate 组合模板
     * @return 刷新后的组合模板
     */
    MultipleTemplate refreshMultipleTemplate(MultipleTemplate multipleTemplate);

    /**
     * 刷新组合模板 将容器中的模板 并将模板写入到配置文件当中
     * @param templateName 组合模板名
     */
    void refreshMultipleTemplate(String templateName);

    /**
     * 删除组合模板
     * @param templateName 组合模板名
     */
    void removeMultipleTemplate(String templateName);

}
