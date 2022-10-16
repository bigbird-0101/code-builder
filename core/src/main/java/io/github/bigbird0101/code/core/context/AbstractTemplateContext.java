package io.github.bigbird0101.code.core.context;

import io.github.bigbird0101.code.core.config.AbstractEnvironment;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.context.aware.TemplateContextProvider;
import io.github.bigbird0101.code.core.event.BasicCodeEvent;
import io.github.bigbird0101.code.core.event.BasicCodeListener;
import io.github.bigbird0101.code.core.event.EventMulticaster;
import io.github.bigbird0101.code.core.event.SimpleEventMulticaster;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinitionRegistry;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinitionRegistry;
import io.github.bigbird0101.code.core.factory.config.TemplateFactoryPostProcessor;
import io.github.bigbird0101.code.core.factory.config.TemplatePostProcessor;
import io.github.bigbird0101.code.core.factory.*;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fpp
 */
public abstract class AbstractTemplateContext implements ConfigurableTemplateContext, TemplateDefinitionRegistry, MultipleTemplateDefinitionRegistry {
    private static Logger logger= LogManager.getLogger(AbstractTemplateContext.class);

    private final List<TemplateFactoryPostProcessor> templateFactoryPostProcessors=new CopyOnWriteArrayList<>();

    private Environment environment;

    private TemplateScanner allTypeTemplateScanner;

    private EventMulticaster eventMulticaster;

    public AbstractTemplateContext(Environment environment) {
        this(environment, new GenericTemplateScanner());
    }

    public AbstractTemplateContext(Environment environment, TemplateScanner allTypeTemplateScanner) {
        this.environment = environment;
        this.allTypeTemplateScanner = allTypeTemplateScanner;
        TemplateContextProvider.setTemplateContext(this);
    }

    @Override
    public Template getTemplate(String templateName){
        return getTemplateFactory().getTemplate(templateName);
    }

    @Override
    public MultipleTemplate getMultipleTemplate(String templateName){
        return getTemplateFactory().getMultipleTemplate(templateName);
    }

    public EventMulticaster getEventMulticaster() {
        return eventMulticaster;
    }

    public void setEventMulticaster(EventMulticaster eventMulticaster) {
        this.eventMulticaster = eventMulticaster;
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
    public void addBasicCodeListener(BasicCodeListener<?> listener) {
        if(null!=getEventMulticaster()){
            getEventMulticaster().addListener(listener);
        }
    }

    public void publishEvent(BasicCodeEvent basicCodeEvent){
        getEventMulticaster().multicastEvent(basicCodeEvent);
    }

    @Override
    public void refresh(){
        //do scan file package to get TemplateDefinition
        try {
            environment.parse();
            if(logger.isInfoEnabled()) {
                logger.info("Environment {}", environment);
            }
            DefaultListableTemplateFactory templateFactory = obtainFreshTemplateFactory();
            prepareTemplateFactory(templateFactory);
            AllTemplateDefinitionHolder allTemplateDefinitionHolder = allTypeTemplateScanner.scanner(environment.getProperty(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH), environment.getProperty(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH));
            //get TemplateFactory
            //register TemplateDefinition
            registerTemplateDefinitionAndMultipleTemplateDefinition(allTemplateDefinitionHolder, templateFactory);
            //invoke template factory post processor
            invokeTemplateFactory(templateFactory);
            //initEventMulticaster
            initEventMulticaster();
            //Instantiate all remaining template
            finishTemplateFactoryInitialization(templateFactory);
            //finishRefresh
            finishRefresh();
        } catch (CodeConfigException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareTemplateFactory(DefaultListableTemplateFactory templateFactory) {
        TemplateContextAwareProcessor templateContextAwareProcessor=new TemplateContextAwareProcessor(this);
        templateContextAwareProcessor.awareProcessor();
    }

    /**
     * 完成模板容器的刷新
     */
    private void finishRefresh() {
        publishEvent(new TemplateContextInitAfterEvent(this));
    }

    private void initEventMulticaster() {
        this.eventMulticaster=new SimpleEventMulticaster();
    }

    private void invokeTemplateFactory(DefaultListableTemplateFactory templateFactory) {
        for(TemplateFactoryPostProcessor templateFactoryPostProcessor:templateFactoryPostProcessors){
            templateFactoryPostProcessor.postProcessTemplateFactory(templateFactory);
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

    @Override
    public void addTemplateFactoryPostProcessor(TemplateFactoryPostProcessor templateFactoryPostProcessor) {
        this.templateFactoryPostProcessors.remove(templateFactoryPostProcessor);
        this.templateFactoryPostProcessors.add(templateFactoryPostProcessor);
    }

    protected DefaultListableTemplateFactory obtainFreshTemplateFactory() {
        refreshTemplateFactory();
        return (DefaultListableTemplateFactory) getTemplateFactory();
    }

    protected abstract void refreshTemplateFactory();

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

    @Override
    public void addTemplatePostProcessor(TemplatePostProcessor templatePostProcessor) {
        getTemplateFactory().addTemplatePostProcessor(templatePostProcessor);
    }
}