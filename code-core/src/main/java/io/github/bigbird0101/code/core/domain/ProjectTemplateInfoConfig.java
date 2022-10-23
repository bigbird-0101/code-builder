package io.github.bigbird0101.code.core.domain;

import java.util.List;
import java.util.Map;

/**
 * 项目模板详情配置
 * @author Administrator
 */
public class ProjectTemplateInfoConfig {
    private List<DefinedFunctionDomain> definedFunctionDomainList;
    private Map<String, List<String>> templateSelectedGroup;

    public ProjectTemplateInfoConfig(List<DefinedFunctionDomain> definedFunctionDomainList, Map<String, List<String>> templateSelectedGroup) {
        this.definedFunctionDomainList = definedFunctionDomainList;
        this.templateSelectedGroup = templateSelectedGroup;
    }

    @Override
    public String toString() {
        return "ProjectTemplateInfoConfig{" +
                "definedFunctionDomainList=" + definedFunctionDomainList +
                ", templateSelectedGroup=" + templateSelectedGroup +
                '}';
    }

    public Map<String, List<String>> getTemplateSelectedGroup() {
        return templateSelectedGroup;
    }

    public void setTemplateSelectedGroup(Map<String, List<String>> templateSelectedGroup) {
        this.templateSelectedGroup = templateSelectedGroup;
    }

    public List<DefinedFunctionDomain> getDefinedFunctionDomainList() {
        return definedFunctionDomainList;
    }

    public void setDefinedFunctionDomainList(List<DefinedFunctionDomain> definedFunctionDomainList) {
        this.definedFunctionDomainList = definedFunctionDomainList;
    }

}