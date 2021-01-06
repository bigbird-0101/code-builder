package com.fpp.code.factory;

import com.fpp.code.factory.config.TemplateDefinition;
import com.fpp.code.factory.config.TemplateFactory;

public interface OperateTemplateBeanFactory extends TemplateFactory {
    void createTemplate(String templateName, TemplateDefinition templateDefinition);
    void refreshTemplate(String templateName);
}
