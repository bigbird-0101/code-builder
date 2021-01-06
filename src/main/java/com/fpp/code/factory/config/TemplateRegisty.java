package com.fpp.code.factory.config;

import com.fpp.code.template.Template;

public interface TemplateRegisty {
    void registerTemplate(String templateName, Template template);

    Template getTemplate(String templateName);
}
