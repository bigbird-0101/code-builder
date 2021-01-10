package com.fpp.code.core.factory;

import java.io.File;
import java.util.Objects;

/**
 * @author fpp
 */
public class GenerateTemplateDefinition extends AbstractTemplateDefinition {

    private String projectUrl;

    private String module;

    private String sourcesRoot;

    private String srcPackage;

    private String path;

    private String fileSuffixName;

    private boolean isHandleFunction;

    private int filePrefixNameStrategy;

    private File templateFile;

    public GenerateTemplateDefinition() {
    }


    @Override
    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    @Override
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public String getSourcesRoot() {
        return sourcesRoot;
    }

    public void setSourcesRoot(String sourcesRoot) {
        this.sourcesRoot = sourcesRoot;
    }

    @Override
    public String getSrcPackage() {
        return srcPackage;
    }

    public void setSrcPackage(String srcPackage) {
        this.srcPackage = srcPackage;
    }

    public void setTemplateFile(File templateFile) {
        this.templateFile = templateFile;
    }

    @Override
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

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public int getFilePrefixNameStrategy() {
        return filePrefixNameStrategy;
    }

    public void setFilePrefixNameStrategy(int filePrefixNameStrategy) {
        this.filePrefixNameStrategy = filePrefixNameStrategy;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenerateTemplateDefinition)) {
            return false;
        }
        GenerateTemplateDefinition that = (GenerateTemplateDefinition) o;
        return isHandleFunction() == that.isHandleFunction() &&
                getFilePrefixNameStrategy() == that.getFilePrefixNameStrategy() &&
                Objects.equals(getProjectUrl(), that.getProjectUrl()) &&
                Objects.equals(getModule(), that.getModule()) &&
                Objects.equals(getSourcesRoot(), that.getSourcesRoot()) &&
                Objects.equals(getSrcPackage(), that.getSrcPackage()) &&
                Objects.equals(getPath(), that.getPath()) &&
                Objects.equals(getFileSuffixName(), that.getFileSuffixName()) &&
                Objects.equals(getTemplateFile(), that.getTemplateFile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectUrl(), getModule(), getSourcesRoot(), getSrcPackage(), getPath(), getFileSuffixName(), isHandleFunction(), getFilePrefixNameStrategy(), getTemplateFile());
    }
}
