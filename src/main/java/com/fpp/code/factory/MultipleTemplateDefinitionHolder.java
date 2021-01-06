package com.fpp.code.factory;

import com.fpp.code.factory.config.MultipleTemplateDefinition;

public class MultipleTemplateDefinitionHolder {
    private MultipleTemplateDefinition multipleTemplateDefinition;
    private String multipleTemplateName;

    public MultipleTemplateDefinitionHolder(MultipleTemplateDefinition multipleTemplateDefinition, String multipleTemplateName) {
        this.multipleTemplateDefinition = multipleTemplateDefinition;
        this.multipleTemplateName = multipleTemplateName;
    }

    public MultipleTemplateDefinition getMultipleTemplateDefinition() {
        return multipleTemplateDefinition;
    }

    public void setMultipleTemplateDefinition(MultipleTemplateDefinition multipleTemplateDefinition) {
        this.multipleTemplateDefinition = multipleTemplateDefinition;
    }

    public String getMultipleTemplateName() {
        return multipleTemplateName;
    }

    public void setMultipleTemplateName(String multipleTemplateName) {
        this.multipleTemplateName = multipleTemplateName;
    }

    @Override
    public String toString() {
        return "MultipleTemplateDefinitionHolder{" +
                "multipleTemplateDefinition=" + multipleTemplateDefinition +
                ", multipleTemplateName='" + multipleTemplateName + '\'' +
                '}';
    }
}
