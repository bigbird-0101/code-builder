package io.github.bigbird0101.code.core.config;

import io.github.bigbird0101.code.core.template.Template;

/**
 * @author bigbird-0101
 */
public class TemplatePropertySource extends PropertySource<Template> {
    public TemplatePropertySource(String name, Template source) {
        super(name, source);
    }
}
