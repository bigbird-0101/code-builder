package com.fpp.code.core.factory;

import com.fpp.code.core.factory.config.MultipleTemplateDefinition;

import java.util.Objects;

/**
 * @author Administrator
 */
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultipleTemplateDefinitionHolder)) {
            return false;
        }
        MultipleTemplateDefinitionHolder that = (MultipleTemplateDefinitionHolder) o;
        return Objects.equals(getMultipleTemplateDefinition(), that.getMultipleTemplateDefinition()) &&
                Objects.equals(getMultipleTemplateName(), that.getMultipleTemplateName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMultipleTemplateDefinition(), getMultipleTemplateName());
    }

    @Override
    public String toString() {
        return "MultipleTemplateDefinitionHolder{" +
                "multipleTemplateDefinition=" + multipleTemplateDefinition +
                ", multipleTemplateName='" + multipleTemplateName + '\'' +
                '}';
    }
}
