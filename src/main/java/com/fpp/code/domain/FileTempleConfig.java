package com.fpp.code.domain;

/**
 * 文件模板配置
 * @author Administrator
 */
public class FileTempleConfig {
    private String projectUrl;
    private String srcUrl;
    private String srcUrlPrefix;
    private String daoPackage;
    private String servicePackage;
    private String controllerPackage;
    private String serviceImplPackage;
    private String domainPackage;
    private int typeBuild;
    private int functionBuild;
    private String getByFiledFiledS;

    public String getGetByFiledFiledS() {
        return getByFiledFiledS;
    }

    public void setGetByFiledFiledS(String getByFiledFiledS) {
        this.getByFiledFiledS = getByFiledFiledS;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public String getSrcUrlPrefix() {
        return srcUrlPrefix;
    }

    public void setSrcUrlPrefix(String srcUrlPrefix) {
        this.srcUrlPrefix = srcUrlPrefix;
    }

    public String getDaoPackage() {
        return daoPackage;
    }

    public void setDaoPackage(String daoPackage) {
        this.daoPackage = daoPackage;
    }

    public String getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    public String getServiceImplPackage() {
        return serviceImplPackage;
    }

    public void setServiceImplPackage(String serviceImplPackage) {
        this.serviceImplPackage = serviceImplPackage;
    }

    public String getDomainPackage() {
        return domainPackage;
    }

    public void setDomainPackage(String domainPackage) {
        this.domainPackage = domainPackage;
    }

    public int getTypeBuild() {
        return typeBuild;
    }

    public void setTypeBuild(int typeBuild) {
        this.typeBuild = typeBuild;
    }

    public int getFunctionBuild() {
        return functionBuild;
    }

    public void setFunctionBuild(int functionBuild) {
        this.functionBuild = functionBuild;
    }

    public FileTempleConfig(String projectUrl, String srcUrl, String srcUrlPrefix, String daoPackage, String servicePackage, String controllerPackage, String serviceImplPackage, String domainPackage, int typeBuild, int functionBuild, String getByFiledFiledS) {
        this.projectUrl = projectUrl;
        this.srcUrl = srcUrl;
        this.srcUrlPrefix = srcUrlPrefix;
        this.daoPackage = daoPackage;
        this.servicePackage = servicePackage;
        this.controllerPackage = controllerPackage;
        this.serviceImplPackage = serviceImplPackage;
        this.domainPackage = domainPackage;
        this.typeBuild = typeBuild;
        this.functionBuild = functionBuild;
        this.getByFiledFiledS = getByFiledFiledS;
    }

    @Override
    public String toString() {
        return "FileTemplePoJo{" +
                "projectUrl='" + projectUrl + '\'' +
                ", srcUrl='" + srcUrl + '\'' +
                ", srcUrlPrefix='" + srcUrlPrefix + '\'' +
                ", daoPackage='" + daoPackage + '\'' +
                ", servicePackage='" + servicePackage + '\'' +
                ", controllerPackage='" + controllerPackage + '\'' +
                ", serviceImplPackage='" + serviceImplPackage + '\'' +
                ", domainPackage='" + domainPackage + '\'' +
                ", typeBuild=" + typeBuild +
                ", functionBuild=" + functionBuild +
                '}';
    }
}
