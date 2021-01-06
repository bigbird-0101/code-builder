package com.fpp.code.factory.config;

import com.fpp.code.template.Template;

public interface TemplateFactory {
    Template getTemplate(String templateName);
}
