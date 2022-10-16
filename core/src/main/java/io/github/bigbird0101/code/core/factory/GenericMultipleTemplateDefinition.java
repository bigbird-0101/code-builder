package io.github.bigbird0101.code.core.factory;

import io.github.bigbird0101.code.core.common.ObjectUtils;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;

import java.util.Objects;
import java.util.Set;

/**
 * @author fpp
 * @version 1.0
 */
public class GenericMultipleTemplateDefinition implements MultipleTemplateDefinition {

    private Set<String> templateNames;

    @Override
    public Set<String> getTemplateNames() {
        return templateNames;
    }

    public void setTemplateNames(Set<String> templateNames) {
        this.templateNames = templateNames;
    }

    @Override
    public Object clone() {
        return ObjectUtils.deepClone(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenericMultipleTemplateDefinition)) {
            return false;
        }
        GenericMultipleTemplateDefinition that = (GenericMultipleTemplateDefinition) o;
        return Objects.equals(getTemplateNames(), that.getTemplateNames());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTemplateNames());
    }
}
