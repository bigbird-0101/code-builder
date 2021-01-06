package com.fpp.code.factory;

import com.fpp.code.factory.config.TemplateDefinition;

public abstract class AbstractOperateTemplateTemplateFactory extends AbstractTemplateFactory implements OperateTemplateBeanFactory {
    @Override
    public void createTemplate(String templateName, TemplateDefinition templateDefinition) {

    }

    @Override
    public void removeTemplate(String templateName) {
        super.removeTemplate(templateName);
    }

    @Override
    public void refreshTemplate(String templateName) {
        getTemplate(templateName).refresh();
    }
}
