package com.fpp.code.factory;

import com.fpp.code.template.Template;

public abstract class AbstractTemplateFactory extends DefaultTemplateRegistry implements ConfigurableTemplateFactory {
    @Override
    public void removeTemplate(String templateName) {
        removeTemplate(templateName);
    }

    @Override
    public Template getTemplate(String templateName) {
        return getTemplate(templateName);
    }
}
