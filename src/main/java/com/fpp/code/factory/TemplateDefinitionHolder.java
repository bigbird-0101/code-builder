package com.fpp.code.factory;

import com.fpp.code.factory.config.TemplateDefinition;

public class TemplateDefinitionHolder {
    private TemplateDefinition templateDefinition;
    private String templateName;

    public TemplateDefinitionHolder(TemplateDefinition templateDefinition, String templateName) {
        this.templateDefinition = templateDefinition;
        this.templateName = templateName;
    }

    public TemplateDefinition getTemplateDefinition() {
        return templateDefinition;
    }

    public void setTemplateDefinition(TemplateDefinition templateDefinition) {
        this.templateDefinition = templateDefinition;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public String toString() {
        return "TemplateDefinitionHolder{" +
                "templateDefinition=" + templateDefinition +
                ", templateName='" + templateName + '\'' +
                '}';
    }
}
