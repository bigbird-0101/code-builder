package com.fpp.code.factory;

import com.fpp.code.config.Environment;
import com.fpp.code.factory.config.TemplateFactory;
import com.fpp.code.template.Template;

public abstract class AbstractTemplateContext implements ConfigurableTemplateContext{

    private Environment environment;

    private TemplateFactory templateFactory;

    public AbstractTemplateContext(Environment environment) {
        this(environment,new DefaultListableTemplateFactory());
    }

    public AbstractTemplateContext(Environment environment, TemplateFactory templateFactory) {
        this.environment = environment;
        this.templateFactory = templateFactory;
        this.refresh();
    }

    @Override
    public TemplateFactory getTemplateFactory() {
        return templateFactory;
    }

    @Override
    public Template getTemplate(String templateName) {
        return templateFactory.getTemplate(templateName);
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }

    @Override
    public void removeTemplate(String templateName) {
        if(templateFactory instanceof DefaultListableTemplateFactory){
            DefaultListableTemplateFactory templateFactoryDefault=(DefaultListableTemplateFactory)templateFactory;
            templateFactoryDefault.removeTemplate(templateName);
        }
    }

    @Override
    public void refresh() {
        //do scan file package to get TemplateDefinition
        //get TemplateFactory
        //register TemplateDefinition
        //Instantiate all remaining template
    }
}
