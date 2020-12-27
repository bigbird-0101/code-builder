package com.fpp.code.template;

import com.fpp.code.config.CodeConfigException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 抽象模板工厂  提取公共的模板对象
 */
public abstract class AbstractTemplateFactory implements TemplateFactory {

    private List<Template> templateList;
    private List<TemplateConfigDomain> templateConfigList;

    public List<TemplateConfigDomain> getTemplateConfigList() {
        return templateConfigList;
    }

    public void setTemplateConfigList(List<TemplateConfigDomain> templateConfigList) {
        this.templateConfigList = templateConfigList;
    }

    public AbstractTemplateFactory(List<TemplateConfigDomain> templateConfigList) {
        this.templateList=new ArrayList<>(10);
        this.templateConfigList=templateConfigList;
    }

    public List<Template> getDefaultTemplate() throws IOException, CodeConfigException {
        if(null!=templateConfigList&&!templateConfigList.isEmpty()) {
            for (TemplateConfigDomain templateConfig:templateConfigList) {
                AbstractTemplate template = new DefaultHandleFunctionTemplate();
                if (templateConfig.getIsHandleFunction() == 0) {
                    template = new DefaultNoHandleFunctionTemplate();
                }
                template.setParentTemplate(getParentTemplate(templateConfig.getParent()));
                template.setPath(templateConfig.getPath());
                template.setTemplateName(templateConfig.getName());
                template.setTemlateFilePathUrl(templateConfig.getUrl());
                template.refresh();
                if (templateConfig.getFilePrefixNameStrategyType() == 0) {
                    template.setTemplateFilePrefixNameStrategy(new DefaultTemplateFilePrefixNameStrategy());
                } else if (templateConfig.getFilePrefixNameStrategyType() == 1) {
                    template.setTemplateFilePrefixNameStrategy(new OnlySubFourTemplateFilePrefixNameStrategy());
                }
                template.setTemplateFileSuffixName(templateConfig.getFileSuffixName());
                templateList.add(template);
            }
        }
        return templateList;
    }

    @Override
    public List<Template> getTemplates() throws IOException, CodeConfigException {
        return getDefaultTemplate();
    }

    /**
     * 根据父模板名获取父模板对象
     * @param templateName
     * @return
     */
    private Template getParentTemplate(String templateName){
        if(null!=templateList&&!templateList.isEmpty()) {
            Optional optional=templateList.stream().filter(template -> template.getTemplateName().equals(templateName)).findFirst();
            return optional.isPresent()? (Template) optional.get() :null;
        }else{
            return null;
        }
    }
}
