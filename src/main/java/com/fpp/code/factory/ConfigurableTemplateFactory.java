package com.fpp.code.factory;

import com.fpp.code.factory.config.TemplateFactory;

public interface ConfigurableTemplateFactory extends TemplateFactory {
    void removeTemplate(String templateName);
}
