package com.fpp.code.core.factory;

import com.fpp.code.core.config.AbstractEnvironment;
import com.fpp.code.core.config.MultipleTemplatePropertySource;
import com.fpp.code.core.config.TemplatePropertySource;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.exception.CreateTemplateException;
import com.fpp.code.core.factory.config.MultipleTemplateDefinition;
import com.fpp.code.core.factory.config.TemplateDefinition;
import com.fpp.code.core.template.*;
import com.fpp.code.util.Utils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author fpp
 */
public abstract class AbstractOperateTemplateTemplateFactory extends AbstractTemplateFactory implements OperateTemplateBeanFactory {
    @Override
    public Template createTemplate(String templateName, TemplateDefinition templateDefinition){
        Template template;
        boolean noHaveDepend = Utils.isEmpty(templateDefinition.getDependTemplates());
        if(templateDefinition.isHandleFunction()){
            template= noHaveDepend?new DefaultHandleFunctionTemplate():new HaveDependTemplateHandleFunctionTemplate();
        }else{
            template=new DefaultNoHandleFunctionTemplate();
        }
        if(!noHaveDepend){
            if(template instanceof HaveDependTemplateHandleFunctionTemplate) {
                HaveDependTemplateHandleFunctionTemplate haveDependTemplateHandleFunctionTemplate = (HaveDependTemplateHandleFunctionTemplate) template;
                haveDependTemplateHandleFunctionTemplate.setDependTemplates(templateDefinition.getDependTemplates());
            }
        }
        template.setProjectUrl(templateDefinition.getProjectUrl());
        template.setModule(templateDefinition.getModule());
        template.setSrcPackage(templateDefinition.getSrcPackage());
        template.setSourcesRoot(templateDefinition.getSourcesRoot());
        template.setTemplateName(templateName);
        template.setTemplateFile(templateDefinition.getTemplateFile());
        template.setTemplateFileSuffixName(templateDefinition.getFileSuffixName());
        template.setTemplateFilePrefixNameStrategy(templateDefinition.getFilePrefixNameStrategy());
        try {
            template.refresh();
        } catch (IOException e) {
            throw new CreateTemplateException(e);
        }
        registerTemplate(templateName,template);
        return template;
    }

    @Override
    protected MultipleTemplate createMultipleTemplate(String templateName, MultipleTemplateDefinition multipleTemplateDefinition){
        MultipleTemplate multipleTemplate=new GenericMultipleTemplate();
        multipleTemplate.setTemplateName(templateName);
        Set<Template> templates=new LinkedHashSet<>();
        for(String templateNameSon:multipleTemplateDefinition.getTemplateNames()){
            templates.add(getTemplate(templateNameSon));
        }
        multipleTemplate.setTemplates(templates);
        registerMultipleTemplate(templateName,multipleTemplate);
        return multipleTemplate;
    }

    @Override
    public void removeTemplate(String templateName){
        Template template = getTemplate(templateName);
        super.removeTemplate(templateName);
        //刷新模板到配置文件中
        getEnvironment().removePropertySourceSerialize(new TemplatePropertySource(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE,template));
    }

    @Override
    public void refreshTemplate(String templateName) throws CodeConfigException, IOException {
        getTemplate(templateName).refresh();
    }

    @Override
    public Template refreshTemplate(Template template) {
        Template templateNew = replaceTemplate(template);
        //刷新模板到配置文件中
        getEnvironment().refreshPropertySourceSerialize(new TemplatePropertySource(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE,template));
        return templateNew;
    }

    @Override
    public MultipleTemplate refreshMultipleTemplate(MultipleTemplate multipleTemplate) {
        MultipleTemplate multipleTemplateNew = replaceMultipleTemplate(multipleTemplate);
        //刷新模板到配置文件中
        getEnvironment().refreshPropertySourceSerialize(new MultipleTemplatePropertySource(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE,multipleTemplate));
        return multipleTemplateNew;
    }

    @Override
    public void refreshMultipleTemplate(String templateName) throws CodeConfigException, IOException {
        MultipleTemplate multipleTemplate = getMultipleTemplate(templateName);
        if(null!=multipleTemplate){
            for(Template template:multipleTemplate.getTemplates()){
                template.refresh();
            }
        }
    }

    @Override
    public void removeMultipleTemplate(String templateName){
        MultipleTemplate multipleTemplate = getMultipleTemplate(templateName);
        super.removeMultipleTemplate(templateName);
        //刷新模板到配置文件中
        getEnvironment().removePropertySourceSerialize(new MultipleTemplatePropertySource(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE,multipleTemplate));
    }
}