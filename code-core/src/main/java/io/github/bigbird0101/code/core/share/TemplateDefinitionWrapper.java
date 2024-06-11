package io.github.bigbird0101.code.core.share;

import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;

import java.util.List;

/**
 * @author bigbird-0101
 * @date 2024-06-11 22:40
 */
public class TemplateDefinitionWrapper {
    private TemplateDefinition templateDefinition;
    private String templateName;
    private List<TemplateDefinitionWrapper> dependTemplateDefinitionList;

    public TemplateDefinitionWrapper(TemplateDefinition templateDefinition, String templateName, List<TemplateDefinitionWrapper> dependTemplateDefinitionList) {
        this.templateDefinition = templateDefinition;
        this.templateName = templateName;
        this.dependTemplateDefinitionList = dependTemplateDefinitionList;
    }

    public TemplateDefinition getTemplateDefinition() {
        return templateDefinition;
    }

    public void setTemplateDefinition(TemplateDefinition templateDefinition) {
        this.templateDefinition = templateDefinition;
    }

    public List<TemplateDefinitionWrapper> getDependTemplateDefinitionList() {
        return dependTemplateDefinitionList;
    }

    public void setDependTemplateDefinitionList(List<TemplateDefinitionWrapper> dependTemplateDefinitionList) {
        this.dependTemplateDefinitionList = dependTemplateDefinitionList;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
