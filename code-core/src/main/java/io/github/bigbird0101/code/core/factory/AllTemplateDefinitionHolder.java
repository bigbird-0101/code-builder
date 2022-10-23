package io.github.bigbird0101.code.core.factory;

import java.util.Set;

/**
 * 所有模板类型
 * @author fpp
 * @version 1.0
 */
public class AllTemplateDefinitionHolder {

    private Set<MultipleTemplateDefinitionHolder> multipleTemplateDefinitionHolders;

    private Set<TemplateDefinitionHolder> templateDefinitionHolders;

    public AllTemplateDefinitionHolder() {
    }

    public AllTemplateDefinitionHolder(Set<MultipleTemplateDefinitionHolder> multipleTemplateDefinitionHolders, Set<TemplateDefinitionHolder> templateDefinitionHolders) {
        this.multipleTemplateDefinitionHolders = multipleTemplateDefinitionHolders;
        this.templateDefinitionHolders = templateDefinitionHolders;
    }

    public Set<MultipleTemplateDefinitionHolder> getMultipleTemplateDefinitionHolders() {
        return multipleTemplateDefinitionHolders;
    }

    public void setMultipleTemplateDefinitionHolders(Set<MultipleTemplateDefinitionHolder> multipleTemplateDefinitionHolders) {
        this.multipleTemplateDefinitionHolders = multipleTemplateDefinitionHolders;
    }

    public Set<TemplateDefinitionHolder> getTemplateDefinitionHolders() {
        return templateDefinitionHolders;
    }

    public void setTemplateDefinitionHolders(Set<TemplateDefinitionHolder> templateDefinitionHolders) {
        this.templateDefinitionHolders = templateDefinitionHolders;
    }

    @Override
    public String toString() {
        return "AllTemplateDefinitionHolder{" +
                "multipleTemplateDefinitionHolders=" + multipleTemplateDefinitionHolders +
                ", templateDefinitionHolders=" + templateDefinitionHolders +
                '}';
    }
}
