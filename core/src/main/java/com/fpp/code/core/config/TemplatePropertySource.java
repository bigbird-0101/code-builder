package com.fpp.code.core.config;

import com.fpp.code.core.template.Template;

public class TemplatePropertySource extends PropertySource<Template> {
    public TemplatePropertySource(String name, Template source) {
        super(name, source);
    }
}
