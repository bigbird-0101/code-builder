package com.fpp.code.core.factory;

import com.fpp.code.core.template.TemplateFilePrefixNameStrategy;

import java.io.File;
import java.util.Objects;
import java.util.Set;

/**
 * @author fpp
 */
public class GenericTemplateDefinition extends AbstractTemplateDefinition {

    private String projectUrl;

    private String module;

    private String sourcesRoot;

    private String srcPackage;

    private String fileSuffixName;

    private boolean isHandleFunction;

    private TemplateFilePrefixNameStrategy filePrefixNameStrategy;

    private File templateFile;

    private Set<String> dependTemplates;

    public GenericTemplateDefinition() {
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

    public void setDependTemplates(Set<String> dependTemplates) {
        this.dependTemplates = dependTemplates;
    }

    @Override
    public Set<String> getDependTemplates() {
        return dependTemplates;
    }

    public void setHandleFunction(boolean handleFunction) {
        isHandleFunction = handleFunction;
    }

    @Override
    public TemplateFilePrefixNameStrategy getFilePrefixNameStrategy() {
        return filePrefixNameStrategy;
    }

    public void setFilePrefixNameStrategy(TemplateFilePrefixNameStrategy filePrefixNameStrategy) {
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
        if (!(o instanceof GenericTemplateDefinition)) {
            return false;
        }
        GenericTemplateDefinition that = (GenericTemplateDefinition) o;
        return isHandleFunction() == that.isHandleFunction() &&
                getFilePrefixNameStrategy() == that.getFilePrefixNameStrategy() &&
                Objects.equals(getProjectUrl(), that.getProjectUrl()) &&
                Objects.equals(getModule(), that.getModule()) &&
                Objects.equals(getSourcesRoot(), that.getSourcesRoot()) &&
                Objects.equals(getSrcPackage(), that.getSrcPackage()) &&
                Objects.equals(getFileSuffixName(), that.getFileSuffixName()) &&
                Objects.equals(getTemplateFile(), that.getTemplateFile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectUrl(), getModule(), getSourcesRoot(), getSrcPackage(),getFileSuffixName(), isHandleFunction(), getFilePrefixNameStrategy(), getTemplateFile());
    }
}
