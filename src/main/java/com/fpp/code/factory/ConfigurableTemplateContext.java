package com.fpp.code.factory;

import com.fpp.code.config.Environment;
import com.fpp.code.factory.config.TemplateContext;
import com.fpp.code.factory.config.TemplateFactory;

public interface ConfigurableTemplateContext extends TemplateContext {

    void setEnvironment(Environment environment);

    TemplateFactory getTemplateFactory();

    void refresh();
}
