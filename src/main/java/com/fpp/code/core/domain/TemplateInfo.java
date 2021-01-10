package com.fpp.code.core.domain;

import java.io.File;

public class TemplateInfo {

    private String projectUrl;

    private String moduleName;

    private String srcPackagePathName;

    private String srcPackageName;

    private String templateName;

    private String fileSuffixName;

    private boolean isHandleFunction;

    private File templateFile;

    public TemplateInfo(String projectUrl, String moduleName, String srcPackagePathName, String srcPackageName, String templateName, String fileSuffixName, boolean isHandleFunction, File templateFile) {
        this.projectUrl = projectUrl;
        this.moduleName = moduleName;
        this.srcPackagePathName = srcPackagePathName;
        this.srcPackageName = srcPackageName;
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

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getSrcPackagePathName() {
        return srcPackagePathName;
    }

    public void setSrcPackagePathName(String srcPackagePathName) {
        this.srcPackagePathName = srcPackagePathName;
    }

    public String getSrcPackageName() {
        return srcPackageName;
    }

    public void setSrcPackageName(String srcPackageName) {
        this.srcPackageName = srcPackageName;
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

    public boolean isHandleFunction() {
        return isHandleFunction;
    }

    public void setHandleFunction(boolean handleFunction) {
        isHandleFunction = handleFunction;
    }

    public File getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(File templateFile) {
        this.templateFile = templateFile;
    }

    @Override
    public String toString() {
        return "TemplateInfo{" +
                "projectUrl='" + projectUrl + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", srcPackagePathName='" + srcPackagePathName + '\'' +
                ", srcPackageName='" + srcPackageName + '\'' +
                ", templateName='" + templateName + '\'' +
                ", fileSuffixName='" + fileSuffixName + '\'' +
                ", isHandleFunction=" + isHandleFunction +
                ", templateFile=" + templateFile +
                '}';
    }
}
