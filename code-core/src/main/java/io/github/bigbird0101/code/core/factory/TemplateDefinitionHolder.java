package io.github.bigbird0101.code.core.factory;

import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;

import java.util.Objects;

/**
 * @author fpp
 */
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemplateDefinitionHolder that)) {
            return false;
        }
        return Objects.equals(getTemplateDefinition(), that.getTemplateDefinition()) &&
                Objects.equals(getTemplateName(), that.getTemplateName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTemplateDefinition(), getTemplateName());
    }

    @Override
    public String toString() {
        return "TemplateDefinitionHolder{" +
                "templateDefinition=" + templateDefinition +
                ", templateName='" + templateName + '\'' +
                '}';
    }
}
