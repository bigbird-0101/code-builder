package io.github.bigbird0101.code.core.factory;

import cn.hutool.core.util.ServiceLoaderUtil;
import io.github.bigbird0101.code.core.config.aware.EnvironmentAware;
import io.github.bigbird0101.code.core.exception.NoSuchMultipleTemplateDefinitionException;
import io.github.bigbird0101.code.core.exception.NoSuchTemplateDefinitionException;
import io.github.bigbird0101.code.core.factory.aware.TemplateFactoryAware;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplatePostProcessor;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.core.template.TemplateTraceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fpp
 */
public abstract class AbstractTemplateFactory extends DefaultTemplateRegistry implements ConfigurableTemplateFactory {
    private static final Logger logger= LogManager.getLogger(AbstractTemplateFactory.class);

    private final transient List<TemplatePostProcessor> templatePostProcessors=new CopyOnWriteArrayList<>();
    {
        templatePostProcessors.addAll(ServiceLoaderUtil.loadList(TemplatePostProcessor.class));
    }

    public void invokeBaseAware(Object object) {
        if(object instanceof TemplateFactoryAware){
            TemplateFactoryAware templateFactoryAware= (TemplateFactoryAware) object;
            templateFactoryAware.setTemplateFactory(AbstractTemplateFactory.this);
        }
        if(object instanceof EnvironmentAware){
            EnvironmentAware environmentAware= (EnvironmentAware) object;
            environmentAware.setEnvironment(getEnvironment());
        }
    }

    @Override
    public void removeTemplate(String templateName){
        super.removeTemplate(templateName);
    }

    @Override
    public Template getTemplate(String templateName){
        return doGetTemplate(templateName);
    }

    @Override
    public MultipleTemplate getMultipleTemplate(String templateName){
        return doGetMultipleTemplate(templateName);
    }

    protected MultipleTemplate doGetMultipleTemplate(String templateName){
        MultipleTemplate multipleTemplate = getSingletonMultipleTemplate(templateName);
        if (null == multipleTemplate) {
            MultipleTemplateDefinition multipleTemplateDefinition = getMultipleTemplateDefinition(templateName);
            if(null==multipleTemplateDefinition) {
                throw new NoSuchMultipleTemplateDefinitionException("not found " + templateName + " multiple template definition");
            }
            multipleTemplate = createMultipleTemplate(templateName, multipleTemplateDefinition);
        }
        return multipleTemplate;
    }

    protected Template doGetTemplate(String templateName){
        Template template = getSingletonTemplate(templateName);
        if (null == template) {
            TemplateDefinition templateDefinition = getTemplateDefinition(templateName);
            if(null==templateDefinition) {
                throw new NoSuchTemplateDefinitionException("not found "+templateName+" template definition");
            }
            template = createTemplate(templateName, templateDefinition);
        }
        logger.info("doGetTemplate {}",templateName);
        TemplateTraceContext.setCurrentTemplate(template);
        return template;
    }


    @Override
    public void addTemplatePostProcessor(TemplatePostProcessor templatePostProcessor) {
        this.templatePostProcessors.remove(templatePostProcessor);
        this.templatePostProcessors.add(templatePostProcessor);
    }

    public List<TemplatePostProcessor> getTemplatePostProcessors() {
        return templatePostProcessors;
    }

    @Override
    public void removeMultipleTemplate(String templateName){
        super.removeMultipleTemplate(templateName);
    }
    /**
     * 获取组合模板定义
     *
     * @param templateName
     * @return
     */
    protected abstract MultipleTemplateDefinition getMultipleTemplateDefinition(String templateName);

    /**
     * 创建组合模板
     *
     * @param templateName
     * @param multipleTemplateDefinition
     * @return
     */
    protected abstract MultipleTemplate createMultipleTemplate(String templateName, MultipleTemplateDefinition multipleTemplateDefinition);

    /**
     * 获取模板定义
     *
     * @param templateName
     * @return
     */
    protected abstract TemplateDefinition getTemplateDefinition(String templateName);

    /**
     * 创建模板
     * @param templateName 模板名
     * @param templateDefinition 模板定义
     * @return 返回的模板对象
     */
    protected abstract Template createTemplate(String templateName, TemplateDefinition templateDefinition);
}