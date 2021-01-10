package com.fpp.code.core.factory;

import com.fpp.code.core.factory.config.MultipleTemplateDefinition;

import java.util.Objects;
import java.util.Set;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/1/6 19:52
 */
public class GenerateMultipleTemplateDefinition implements MultipleTemplateDefinition {

    private Set<String> templateNames;

    @Override
    public Set<String> getTemplateNames() {
        return templateNames;
    }

    public void setTemplateNames(Set<String> templateNames) {
        this.templateNames = templateNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenerateMultipleTemplateDefinition)) {
            return false;
        }
        GenerateMultipleTemplateDefinition that = (GenerateMultipleTemplateDefinition) o;
        return Objects.equals(getTemplateNames(), that.getTemplateNames());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTemplateNames());
    }
}
