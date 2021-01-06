package com.fpp.code.factory;

import com.fpp.code.factory.config.TemplateDefinition;

import java.io.File;

public abstract class AbstractTemplateDefinition implements TemplateDefinition {

    private String projectUrl;

    private String module;

    private String sourcesRoot;

    private String srcPackage;

    private String templateName;

    private String fileSuffixName;

    private boolean isHandleFunction;

    private File templateFile;

    public AbstractTemplateDefinition(String projectUrl, String module, String sourcesRoot, String srcPackage, String templateName, String fileSuffixName, boolean isHandleFunction, File templateFile) {
        this.projectUrl = projectUrl;
        this.module = module;
        this.sourcesRoot = sourcesRoot;
        this.srcPackage = srcPackage;
        this.templateName = templateName;
        this.fileSuffixName = fileSuffixName;
        this.isHandleFunction = isHandleFunction;
        this.templateFile = templateFile;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSourcesRoot() {
        return sourcesRoot;
    }

    public void setSourcesRoot(String sourcesRoot) {
        this.sourcesRoot = sourcesRoot;
    }

    public String getSrcPackage() {
        return srcPackage;
    }

    public void setSrcPackage(String srcPackage) {
        this.srcPackage = srcPackage;
    }

    public void setTemplateFile(File templateFile) {
        this.templateFile = templateFile;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getFileSuffixName() {
        return fileSuffixName;
    }

    public void setFileSuffixName(String fileSuffixName) {
        this.fileSuffixName = fileSuffixName;
    }

    @Override
    public boolean isHandleFunction() {
        return isHandleFunction;
    }

    public void setHandleFunction(boolean handleFunction) {
        isHandleFunction = handleFunction;
    }

    /**
     * 获取模板内容
     * @return
     */
    @Override
    public File getTemplateFile() {
        return this.templateFile;
    }

    @Override
    public String getTemplateResultBuildPath() {
        return this.getProjectUrl()+"/"+this.getModule()+"/"+this.getSourcesRoot()+"/"+
                this.getSrcPackage();
    }

    @Override
    public String getTemplateResultFileNameSuffix() {
        return this.getFileSuffixName();
    }
}
