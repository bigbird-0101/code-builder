package io.github.bigbird0101.code.core.context;

import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.event.BasicCodeEvent;
import io.github.bigbird0101.code.core.event.BasicCodeListener;
import io.github.bigbird0101.code.core.event.EventMulticaster;
import io.github.bigbird0101.code.core.event.SimpleEventMulticaster;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.factory.AllTemplateDefinitionHolder;
import io.github.bigbird0101.code.core.factory.ConfigurableTemplateContext;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.GenericTemplateScanner;
import io.github.bigbird0101.code.core.factory.MultipleTemplateDefinitionHolder;
import io.github.bigbird0101.code.core.factory.TemplateDefinitionHolder;
import io.github.bigbird0101.code.core.factory.TemplateScanner;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinitionRegistry;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinitionRegistry;
import io.github.bigbird0101.code.core.factory.config.TemplateFactoryPostProcessor;
import io.github.bigbird0101.code.core.factory.config.TemplatePostProcessor;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH;
import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH;

/**
 * @author fpp
 */
public abstract class AbstractTemplateContext implements ConfigurableTemplateContext, TemplateDefinitionRegistry, MultipleTemplateDefinitionRegistry {
    private static final Logger logger = LogManager.getLogger(AbstractTemplateContext.class);

    private final List<TemplateFactoryPostProcessor> templateFactoryPostProcessors=new CopyOnWriteArrayList<>();

    private Environment environment;

    private final TemplateScanner allTypeTemplateScanner;

    private EventMulticaster eventMulticaster;

    public AbstractTemplateContext(Environment environment) {
        this(environment, new GenericTemplateScanner());
    }

    public AbstractTemplateContext(Environment environment, TemplateScanner allTypeTemplateScanner) {
        this.environment = environment;
        this.allTypeTemplateScanner = allTypeTemplateScanner;
        AbstractTemplateContextProvider.setTemplateContext(this);
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

    @Override
    public void addListener(BasicCodeListener<?> listener) {
        addBasicCodeListener(listener);
    }

    @Override
    public void removeListener(BasicCodeListener<?> listener) {
        getEventMulticaster().removeListener(listener);
    }

    @Override
    public void multicastEvent(BasicCodeEvent event) {
        getEventMulticaster().multicastEvent(event);
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
            String templateFilesPath = environment.getProperty(DEFAULT_CORE_TEMPLATE_FILES_PATH);
            String templateConfigFilePath = environment.getProperty(DEFAULT_CORE_TEMPLATE_PATH);
            AllTemplateDefinitionHolder allTemplateDefinitionHolder = allTypeTemplateScanner.scanner(templateFilesPath,
                    templateConfigFilePath);
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

    /**
     * 刷新模板工厂
     */
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