package com.fpp.code.template;

import com.fpp.code.config.CodeConfigException;
import com.fpp.code.config.ProjectFileConfig;

import java.io.IOException;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/5/19 11:14
 */
public class DefaultNoHandleFunctionTemplate extends NoHandleFunctionTemplate {

    public DefaultNoHandleFunctionTemplate() throws IOException, CodeConfigException {
        super(null);
    }

    public DefaultNoHandleFunctionTemplate(String templateName,String templeFileName, ProjectFileConfig projectFileConfig,Template parentTemplate,String path) throws IOException, CodeConfigException {
        super(templeFileName,projectFileConfig);
        this.setTemplateName(templateName);
        this.setParentTemplate(parentTemplate);
        this.setPath(path);
    }

    public DefaultNoHandleFunctionTemplate(String templateName,String templeFileName,Template parentTemplate,String path) throws IOException, CodeConfigException {
        super(templeFileName);
        this.setTemplateName(templateName);
        this.setParentTemplate(parentTemplate);
        this.setPath(path);
    }

    public DefaultNoHandleFunctionTemplate(String templateName,String templeFileName,String path) throws IOException, CodeConfigException {
        super(templeFileName);
        this.setTemplateName(templateName);
        this.setParentTemplate(null);
        this.setPath(path);
    }

}
