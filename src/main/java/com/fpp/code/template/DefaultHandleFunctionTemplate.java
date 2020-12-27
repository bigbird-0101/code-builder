package com.fpp.code.template;

import com.fpp.code.config.CodeConfigException;
import com.fpp.code.config.ProjectFileConfig;

import java.io.IOException;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 18:24
 */
public class DefaultHandleFunctionTemplate extends HandleFunctionTemplate {

    public DefaultHandleFunctionTemplate() throws IOException, CodeConfigException {
        super(null);
    }

    public DefaultHandleFunctionTemplate(String templateName,String templeFileName,String path) throws IOException, CodeConfigException {
        super(templeFileName);
        this.setTemplateName(templateName);
        this.setParentTemplate(null);
        this.setPath(path);
    }

    public DefaultHandleFunctionTemplate(String templateName,String templeFileName,ResolverStrategy resolverStrategy,String path) throws IOException, CodeConfigException {
        super(templeFileName);
        this.setTemplateName(templateName);
        this.setParentTemplate(null);
        this.setResolverStrategy(resolverStrategy);
        this.setPath(path);
    }

    public DefaultHandleFunctionTemplate(String templateName,String templeFileName,Template parentTemplate,ResolverStrategy resolverStrategy,String path) throws IOException, CodeConfigException {
        super(templeFileName);
        this.setTemplateName(templateName);
        this.setParentTemplate(parentTemplate);
        this.setResolverStrategy(resolverStrategy);
        this.setPath(path);
    }

    public DefaultHandleFunctionTemplate(String templateName,String templeFileName,Template parentTemplate,String path) throws IOException, CodeConfigException {
        super(templeFileName);
        this.setTemplateName(templateName);
        this.setParentTemplate(parentTemplate);
        this.setPath(path);
    }

    public DefaultHandleFunctionTemplate(String templateName,String templeFileName, ProjectFileConfig projectFileConfig,Template parentTemplate,String path) throws IOException, CodeConfigException {
        super(templeFileName, projectFileConfig);
        this.setTemplateName(templateName);
        this.setParentTemplate(parentTemplate);
        this.setPath(path);
    }


    /**
     * 设置模板解析策略
     *
     * @param resolverStrategy 模板解析策略
     */
    @Override
    public void setResolverStrategy(ResolverStrategy resolverStrategy) {
        this.resolverStrategy=resolverStrategy;
    }
}
