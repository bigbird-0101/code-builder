package com.fpp.code.core.domain;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * 项目模板详情配置
 * @author Administrator
 */
public class ProjectTemplateInfoConfig {
    private JSONArray handleTemplateBuild;
    private JSONArray noHandleTemplateBuild;
    private String projectCompleteUrl;
    private String projectTargetPackageurl;
    private List<DefinedFunctionDomain> definedFunctionDomainList;

    public ProjectTemplateInfoConfig(JSONArray handleTemplateBuild, JSONArray noHandleTemplateBuild, String projectCompleteUrl, String projectTargetPackageurl, List<DefinedFunctionDomain> definedFunctionDomainList) {
        this.handleTemplateBuild = handleTemplateBuild;
        this.noHandleTemplateBuild = noHandleTemplateBuild;
        this.projectCompleteUrl = projectCompleteUrl;
        this.projectTargetPackageurl = projectTargetPackageurl;
        this.definedFunctionDomainList = definedFunctionDomainList;
    }

    public JSONArray getHandleTemplateBuild() {
        return handleTemplateBuild;
    }

    public void setHandleTemplateBuild(JSONArray handleTemplateBuild) {
        this.handleTemplateBuild = handleTemplateBuild;
    }

    public JSONArray getNoHandleTemplateBuild() {
        return noHandleTemplateBuild;
    }

    public void setNoHandleTemplateBuild(JSONArray noHandleTemplateBuild) {
        this.noHandleTemplateBuild = noHandleTemplateBuild;
    }

    public String getProjectCompleteUrl() {
        return projectCompleteUrl;
    }

    public void setProjectCompleteUrl(String projectCompleteUrl) {
        this.projectCompleteUrl = projectCompleteUrl;
    }

    public String getProjectTargetPackageurl() {
        return projectTargetPackageurl;
    }

    public void setProjectTargetPackageurl(String projectTargetPackageurl) {
        this.projectTargetPackageurl = projectTargetPackageurl;
    }

    public List<DefinedFunctionDomain> getDefinedFunctionDomainList() {
        return definedFunctionDomainList;
    }

    public void setDefinedFunctionDomainList(List<DefinedFunctionDomain> definedFunctionDomainList) {
        this.definedFunctionDomainList = definedFunctionDomainList;
    }

    @Override
    public String toString() {
        return "ProjectTemplateInfoConfig{" +
                "handleTemplateBuild=" + handleTemplateBuild +
                ", noHandleTemplateBuild=" + noHandleTemplateBuild +
                ", projectCompleteUrl='" + projectCompleteUrl + '\'' +
                ", projectTargetPackageurl='" + projectTargetPackageurl + '\'' +
                ", definedFunctionDomainList=" + definedFunctionDomainList +
                '}';
    }
}