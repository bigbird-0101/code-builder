package com.fpp.code.core.factory;

import com.fpp.code.core.common.ClassUtils;
import com.fpp.code.core.common.ObjectUtils;
import com.fpp.code.core.config.AbstractEnvironment;
import com.fpp.code.core.config.MultipleTemplatePropertySource;
import com.fpp.code.core.config.TemplatePropertySource;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.exception.CreateTemplateException;
import com.fpp.code.core.factory.config.MultipleTemplateDefinition;
import com.fpp.code.core.factory.config.TemplateDefinition;
import com.fpp.code.core.factory.config.TemplatePostProcessor;
import com.fpp.code.core.template.*;
import com.fpp.code.util.Utils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author fpp
 */
public abstract class AbstractOperateTemplateTemplateFactory extends AbstractTemplateFactory implements OperateTemplateBeanFactory {
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Override
    public Template createTemplate(String templateName, TemplateDefinition templateDefinition) {
        RootTemplateDefinition rootTemplateDefinition= (RootTemplateDefinition) templateDefinition;
        Template template = createTemplateInstant(rootTemplateDefinition, templateName);
        beforeInstantiation(template, templateName);
        initTemplatePropertyValues(templateName, template, rootTemplateDefinition);
        afterInstantiation(template, templateName);
        registerTemplate(templateName, template);
        return template;
    }

    private void afterInstantiation(Template existingTemplate, String templateName) {
        for (TemplatePostProcessor templatePostProcessor : getTemplatePostProcessors()) {
            Template current = templatePostProcessor.postProcessAfterInstantiation(existingTemplate, templateName);
            if (null == current) {
                return;
            }
        }
    }

    private void beforeInstantiation(Template existingTemplate, String templateName) {
        for (TemplatePostProcessor templatePostProcessor : getTemplatePostProcessors()) {
            Template current = templatePostProcessor.postProcessBeforeInstantiation(existingTemplate, templateName);
            if (null == current) {
                return;
            }
        }
    }

    private Template createTemplateInstant(RootTemplateDefinition templateDefinition, String templateName) {
        try {
            if(Utils.isEmpty(templateDefinition.getTemplateClassName())){
                boolean noHaveDepend = Utils.isEmpty(templateDefinition.getDependTemplates());
                if(templateDefinition.isHandleFunction()){
                    return noHaveDepend ? new DefaultHandleFunctionTemplate() : new HaveDependTemplateHandleFunctionTemplate();
                }else{
                    return noHaveDepend ? new DefaultNoHandleFunctionTemplate() : new HaveDependTemplateNoHandleFunctionTemplate();
                }
            }else {
                Class<?> templateClass = beanClassLoader.loadClass(templateDefinition.getTemplateClassName());
                templateDefinition.setTemplateClass(templateClass);
                return (Template) templateClass.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new CreateTemplateException(e);
        }
    }

    private void initTemplatePropertyValues(String templateName, Template template, RootTemplateDefinition templateDefinition) {
        ObjectUtils.copyProperties(templateDefinition,template);
        template.setTemplateName(templateName);
        template.refresh();
    }

    @Override
    protected MultipleTemplate createMultipleTemplate(String templateName, MultipleTemplateDefinition multipleTemplateDefinition) {
        MultipleTemplate multipleTemplate = new GenericMultipleTemplate();
        multipleTemplate.setTemplateName(templateName);
        Set<Template> templates = new LinkedHashSet<>();
        for (String templateNameSon : multipleTemplateDefinition.getTemplateNames()) {
            templates.add(getTemplate(templateNameSon));
        }
        multipleTemplate.setTemplates(templates);
        registerMultipleTemplate(templateName, multipleTemplate);
        return multipleTemplate;
    }

    @Override
    public void removeTemplate(String templateName) {
        Template template = getTemplate(templateName);
        super.removeTemplate(templateName);
        //刷新模板到配置文件中
        getEnvironment().removePropertySourceSerialize(new TemplatePropertySource(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE, template));
    }

    @Override
    public void refreshTemplate(String templateName) throws CodeConfigException, IOException {
        getTemplate(templateName).refresh();
    }

    @Override
    public Template refreshTemplate(Template template) {
        Template templateNew = replaceTemplate(template);
        //刷新模板到配置文件中
        getEnvironment().refreshPropertySourceSerialize(new TemplatePropertySource(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE, template));
        return templateNew;
    }

    @Override
    public MultipleTemplate refreshMultipleTemplate(MultipleTemplate multipleTemplate) {
        MultipleTemplate multipleTemplateNew = replaceMultipleTemplate(multipleTemplate);
        //刷新模板到配置文件中
        getEnvironment().refreshPropertySourceSerialize(new MultipleTemplatePropertySource(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE, multipleTemplate));
        return multipleTemplateNew;
    }

    @Override
    public void refreshMultipleTemplate(String templateName) throws CodeConfigException, IOException {
        MultipleTemplate multipleTemplate = getMultipleTemplate(templateName);
        if (null != multipleTemplate) {
            for (Template template : multipleTemplate.getTemplates()) {
                template.refresh();
            }
        }
    }

    @Override
    public void removeMultipleTemplate(String templateName) {
        MultipleTemplate multipleTemplate = getMultipleTemplate(templateName);
        super.removeMultipleTemplate(templateName);
        //刷新模板到配置文件中
        getEnvironment().removePropertySourceSerialize(new MultipleTemplatePropertySource(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE, multipleTemplate));
    }
}