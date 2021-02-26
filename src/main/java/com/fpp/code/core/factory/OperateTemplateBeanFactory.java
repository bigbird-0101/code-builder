package com.fpp.code.core.factory;

import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.factory.config.TemplateDefinition;
import com.fpp.code.core.factory.config.TemplateFactory;
import com.fpp.code.core.template.MultipleTemplate;
import com.fpp.code.core.template.Template;

import java.io.IOException;

public interface OperateTemplateBeanFactory extends TemplateFactory {
    /**
     * 创建模板
     * @param templateName
     * @param templateDefinition
     * @return
     * @throws IOException
     * @throws CodeConfigException
     */
    Template createTemplate(String templateName, TemplateDefinition templateDefinition) throws IOException, CodeConfigException;

    /**
     * 刷新模板 从配置文件中重新刷新模板
     * @param templateName
     * @throws CodeConfigException
     */
    void refreshTemplate(String templateName) throws CodeConfigException, IOException;

    /**
     * 刷新容器中模板 将容器中的模板 并将模板写入到配置文件当中
     * @param template
     * @throws CodeConfigException
     */
    Template refreshTemplate(Template template);
    /**
     * 删除模板
     * @param templateName
     */
    void removeTemplate(String templateName) throws CodeConfigException, IOException;

    /**
     * 刷新容器中组合模板 将容器中的模板 从配置文件中重新刷新模板
     * @param multipleTemplate
     */
    MultipleTemplate refreshMultipleTemplate(MultipleTemplate multipleTemplate);

    /**
     * 刷新组合模板 将容器中的模板 并将模板写入到配置文件当中
     * @param templateName
     */
    void refreshMultipleTemplate(String templateName) throws CodeConfigException, IOException;

    /**
     * 删除组合模板
     * @param templateName 组合模板名
     */
    void removeMultipleTemplate(String templateName) throws CodeConfigException, IOException;

}
