package com.fpp.code.core.factory;

import com.fpp.code.core.template.TemplateFilePrefixNameStrategy;

import java.util.Objects;
import java.util.Set;

/**
 * @author fpp
 */
public class RootTemplateDefinition extends AbstractTemplateDefinition {

    private String templateFileSuffixName;

    private Boolean isHandleFunction;

    private TemplateFilePrefixNameStrategy templateFilePrefixNameStrategy;

    private Set<String> dependTemplates;


    public RootTemplateDefinition() {
    }


    @Override
    public String getTemplateFileSuffixName() {
        return templateFileSuffixName;
    }

    public void setTemplateFileSuffixName(String templateFileSuffixName) {
        this.templateFileSuffixName = templateFileSuffixName;
    }

    @Override
    public boolean isHandleFunction() {
        return isHandleFunction;
    }

    public void setDependTemplates(Set<String> dependTemplates) {
        this.dependTemplates = dependTemplates;
    }

    @Override
    public Set<String> getDependTemplates() {
        return dependTemplates;
    }


    public void setHandleFunction(Boolean handleFunction) {
        isHandleFunction = handleFunction;
    }

    @Override
    public TemplateFilePrefixNameStrategy getTemplateFilePrefixNameStrategy() {
        return templateFilePrefixNameStrategy;
    }

    public void setTemplateFilePrefixNameStrategy(TemplateFilePrefixNameStrategy templateFilePrefixNameStrategy) {
        this.templateFilePrefixNameStrategy = templateFilePrefixNameStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RootTemplateDefinition that = (RootTemplateDefinition) o;
        return isHandleFunction == that.isHandleFunction &&
                templateFileSuffixName.equals(that.templateFileSuffixName) &&
                templateFilePrefixNameStrategy.equals(that.templateFilePrefixNameStrategy) &&
                dependTemplates.equals(that.dependTemplates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), templateFileSuffixName, isHandleFunction, templateFilePrefixNameStrategy, dependTemplates);
    }
}
