package com.fpp.code.core.context;

import com.fpp.code.core.config.AbstractEnvironment;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.factory.*;
import com.fpp.code.core.factory.config.MultipleTemplateDefinitionRegistry;
import com.fpp.code.core.factory.config.TemplateDefinitionRegistry;
import com.fpp.code.core.template.MultipleTemplate;
import com.fpp.code.core.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Set;

/**
 * @author fpp
 */
public abstract class AbstractTemplateContext implements ConfigurableTemplateContext, TemplateDefinitionRegistry, MultipleTemplateDefinitionRegistry {
    private static Logger logger= LogManager.getLogger(AbstractTemplateContext.class);

    private Environment environment;

    private TemplateScanner allTypeTemplateScanner;

    public AbstractTemplateContext(Environment environment) {
        this(environment, new GenericTemplateScanner());
    }

    public AbstractTemplateContext(Environment environment, TemplateScanner allTypeTemplateScanner) {
        this.environment = environment;
        this.allTypeTemplateScanner = allTypeTemplateScanner;
    }

    @Override
    public Template getTemplate(String templateName) throws CodeConfigException, IOException {
        return getTemplateFactory().getTemplate(templateName);
    }

    @Override
    public MultipleTemplate getMultipleTemplate(String templateName) throws CodeConfigException, IOException {
        return getTemplateFactory().getMultipleTemplate(templateName);
    }



    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void refresh() throws CodeConfigException, IOException {
        //do scan file package to get TemplateDefinition
        try {
            environment.parse();
            if(logger.isInfoEnabled()) {
                logger.info("Environment {}", environment);
            }
            DefaultListableTemplateFactory templateFactory = (DefaultListableTemplateFactory) getTemplateFactory();
            AllTemplateDefinitionHolder allTemplateDefinitionHolder = allTypeTemplateScanner.scanner(environment.getProperty(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH), environment.getProperty(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH));
            //get TemplateFactory
            //register TemplateDefinition
            registerTemplateDefinitionAndMultipleTemplateDefinition(allTemplateDefinitionHolder, templateFactory);
            //Instantiate all remaining template
            finishTemplateFactoryInitialization(templateFactory);
        } catch (CodeConfigException | IOException e) {
            throw e;
        }
    }

    protected void finishTemplateFactoryInitialization(DefaultListableTemplateFactory templateFactory) throws CodeConfigException, IOException {
        templateFactory.preInstantiateTemplates();
    }

    protected void registerTemplateDefinitionAndMultipleTemplateDefinition(AllTemplateDefinitionHolder allTemplateDefinitionHolder, DefaultListableTemplateFactory templateFactory) {
        Set<TemplateDefinitionHolder> templateDefinitionHolders = allTemplateDefinitionHolder.getTemplateDefinitionHolders();
        for (TemplateDefinitionHolder templateDefinitionHolder : templateDefinitionHolders) {
            templateFactory.registerTemplateDefinition(templateDefinitionHolder.getTemplateName(), templateDefinitionHolder.getTemplateDefinition());
        }
        Set<MultipleTemplateDefinitionHolder> multipleTemplateDefinitionHolders = allTemplateDefinitionHolder.getMultipleTemplateDefinitionHolders();
        for (MultipleTemplateDefinitionHolder multipleTemplateDefinitionHolder : multipleTemplateDefinitionHolders) {
            templateFactory.registerMultipleTemplateDefinition(multipleTemplateDefinitionHolder.getMultipleTemplateName(), multipleTemplateDefinitionHolder.getMultipleTemplateDefinition());
        }
    }

    /**
     * 获取模板名字集合
     * @return
     */
    @Override
    public Set<String> getTemplateNames(){
        return getTemplateFactory().getTemplateNames();
    }

    /**
     * 获取组合模板名字集合
     * @return
     */
    @Override
    public Set<String> getMultipleTemplateNames(){
        return getTemplateFactory().getMultipleTemplateNames();
    }
}