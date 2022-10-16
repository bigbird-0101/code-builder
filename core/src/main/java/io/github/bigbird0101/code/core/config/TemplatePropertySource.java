package io.github.bigbird0101.code.core.config;

import io.github.bigbird0101.code.core.template.Template;

public class TemplatePropertySource extends PropertySource<Template> {
    public TemplatePropertySource(String name, Template source) {
        super(name, source);
    }
}
