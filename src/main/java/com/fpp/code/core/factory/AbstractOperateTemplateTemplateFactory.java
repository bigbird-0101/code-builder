package com.fpp.code.core.factory;

import com.fpp.code.core.config.AbstractEnvironment;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.GeneratePropertySource;
import com.fpp.code.core.factory.config.MultipleTemplateDefinition;
import com.fpp.code.core.factory.config.TemplateDefinition;
import com.fpp.code.core.template.*;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author fpp
 */
public abstract class AbstractOperateTemplateTemplateFactory extends AbstractTemplateFactory implements OperateTemplateBeanFactory {
    @Override
    public Template createTemplate(String templateName, TemplateDefinition templateDefinition) throws CodeConfigException, IOException {
        Template template;
        if(templateDefinition.isHandleFunction()){
            template=new DefaultHandleFunctionTemplate();
        }else{
            template=new DefaultNoHandleFunctionTemplate();
        }
        template.setProjectUrl(templateDefinition.getProjectUrl());
        template.setModule(templateDefinition.getModule());
        template.setSrcPackage(templateDefinition.getSrcPackage());
        template.setSourcesRoot(templateDefinition.getSourcesRoot());
        template.setTemplateName(templateName);
        template.setTemplateFile(templateDefinition.getTemplateFile());
        template.setTemplateFileSuffixName(templateDefinition.getFileSuffixName());
        template.refresh();
        registerTemplate(templateName,template);
        return template;
    }

    @Override
    protected MultipleTemplate createMultipleTemplate(String templateName, MultipleTemplateDefinition multipleTemplateDefinition) throws CodeConfigException, IOException {
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
    public void removeTemplate(String templateName) throws CodeConfigException, IOException {
        Template template = getTemplate(templateName);
        super.removeTemplate(templateName);
        //刷新模板到配置文件中
        getEnvironment().removePropertySourceSerialize(new GeneratePropertySource<>(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE,template));
    }

    @Override
    public void refreshTemplate(String templateName) throws CodeConfigException, IOException {
        getTemplate(templateName).refresh();
    }

    @Override
    public Template refreshTemplate(Template template) {
        Template templateNew = replaceTemplate(template);
        //刷新模板到配置文件中
        getEnvironment().refreshPropertySourceSerialize(new GeneratePropertySource<>(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE,template));
        return templateNew;
    }

    @Override
    public MultipleTemplate refreshMultipleTemplate(MultipleTemplate multipleTemplate) {
        MultipleTemplate multipleTemplateNew = replaceMultipleTemplate(multipleTemplate);
        //刷新模板到配置文件中
        getEnvironment().refreshPropertySourceSerialize(new GeneratePropertySource<>(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE,multipleTemplate));
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
    public void removeMultipleTemplate(String templateName) throws CodeConfigException, IOException {
        MultipleTemplate multipleTemplate = getMultipleTemplate(templateName);
        super.removeMultipleTemplate(templateName);
        //刷新模板到配置文件中
        getEnvironment().removePropertySourceSerialize(new GeneratePropertySource<>(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE,multipleTemplate));
    }
}