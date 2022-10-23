package io.github.bigbird0101.code.core.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.config.AbstractEnvironment;
import io.github.bigbird0101.code.core.config.MultipleTemplatePropertySource;
import io.github.bigbird0101.code.core.config.TemplatePropertySource;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.exception.CreateTemplateException;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplatePostProcessor;
import io.github.bigbird0101.code.core.template.*;
import io.github.bigbird0101.code.util.Utils;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author fpp
 */
public abstract class AbstractOperateTemplateTemplateFactory extends AbstractTemplateFactory implements OperateTemplateBeanFactory {
    private final ClassLoader beanClassLoader = ClassUtil.getClassLoader();
    @Override
    public Template createTemplate(String templateName, TemplateDefinition templateDefinition) {
        RootTemplateDefinition rootTemplateDefinition= (RootTemplateDefinition) templateDefinition;
        Template template = createTemplateInstant(rootTemplateDefinition);
        Template templateWrapper=beforeInstantiation(template, templateName);
        initTemplatePropertyValues(templateName, templateWrapper, rootTemplateDefinition);
        templateWrapper=afterInstantiation(templateWrapper, templateName);
        registerTemplate(templateName, templateWrapper);
        return template;
    }

    private Template afterInstantiation(Template existingTemplate, String templateName) {
        Template result=existingTemplate;
        for (TemplatePostProcessor templatePostProcessor : getTemplatePostProcessors()) {
            Template current = templatePostProcessor.postProcessAfterInstantiation(existingTemplate, templateName);
            if (null == current) {
                return result;
            }else{
                result=current;
            }
        }
        return result;
    }

    private Template beforeInstantiation(Template existingTemplate, String templateName) {
        Template result=existingTemplate;
        for (TemplatePostProcessor templatePostProcessor : getTemplatePostProcessors()) {
            Template current = templatePostProcessor.postProcessBeforeInstantiation(result, templateName);
            if (null == current) {
                return result;
            }else{
                result=current;
            }
        }
        return result;
    }

    private Template createTemplateInstant(RootTemplateDefinition templateDefinition) {
        Template result;
        try {
            //兼容老的逻辑
            if(Utils.isEmpty(templateDefinition.getTemplateClassName())){
                boolean noHaveDepend = CollectionUtil.isEmpty(templateDefinition.getDependTemplates());
                if(templateDefinition.isHandleFunction()){
                    result = noHaveDepend ? new DefaultHandleFunctionTemplate() : new HaveDependTemplateHandleFunctionTemplate();
                }else{
                    result=noHaveDepend ? new DefaultNoHandleFunctionTemplate() : new HaveDependTemplateNoHandleFunctionTemplate();
                }
            }else {
                Class<?> templateClass = beanClassLoader.loadClass(templateDefinition.getTemplateClassName());
                templateDefinition.setTemplateClass(templateClass);
                result= (Template) templateClass.newInstance();
            }
            invokeBaseAware(result);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new CreateTemplateException(e);
        }
        return result;
    }

    private void initTemplatePropertyValues(String templateName, Template template, RootTemplateDefinition templateDefinition) {
        BeanUtil.copyProperties(templateDefinition,template);
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
        if(getTemplateNames().contains(templateName)) {
            Template template = getTemplate(templateName);
            super.removeTemplate(templateName);
            //刷新模板到配置文件中
            getEnvironment().removePropertySourceSerialize(new TemplatePropertySource(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE, template));
        }
    }

    @Override
    public void refreshTemplate(String templateName) throws CodeConfigException {
        getTemplate(templateName).refresh();
    }

    @Override
    public Template refreshTemplate(Template template) {
        Template templateNew = replaceTemplate(template);
        if(template instanceof AbstractTemplate){
            AbstractTemplate abstractTemplate= (AbstractTemplate) template;
            abstractTemplate.initTemplateVariables();
        }
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
    public void refreshMultipleTemplate(String templateName) throws CodeConfigException {
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

    @Override
    public void destroyTemplates() {
        final Set<String> templateNames = new HashSet<>(getTemplateNames());
        templateNames.stream().filter(StrUtil::isNotBlank).forEach(super::removeTemplate);
    }
}