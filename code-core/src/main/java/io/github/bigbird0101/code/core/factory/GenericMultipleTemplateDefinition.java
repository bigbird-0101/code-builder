package io.github.bigbird0101.code.core.factory;

import cn.hutool.core.util.ObjectUtil;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
        return ObjectUtil.cloneByStream(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenericMultipleTemplateDefinition that)) {
            return false;
        }
        return Objects.equals(getTemplateNames(), that.getTemplateNames());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTemplateNames());
    }

    @Override
    public void refresh(MultipleTemplate multipleTemplate) {
        Set<Template> templates = multipleTemplate.getTemplates();
        setTemplateNames(templates.stream().map(Template::getTemplateName).collect(Collectors.toSet()));
    }
}
