package com.fpp.code.core.template;

import java.util.Map;

/**
 * 模板文件类信息
 * 注意:信息中的模板信息还未替换
 * @author fpp
 * @version 1.0
 * @date 2020/5/20 14:58
 */
public class TemplateFileClassInfo {

    /**
     * 模板文件除了方法内容的前缀 包括属性
     */
    private String templateClassPrefix;
    /**
     * 模板文件后缀除了方法内容和前缀，一般为 "\r\n}"
     */
    private String templateClassSuffix;
    /**
     * key-方法名 value 为 方法体包括注释
     */
    private Map<String,String> functionS;

    public TemplateFileClassInfo(String templateClassPrefix,Map<String, String> functionS) {
        this(templateClassPrefix,"\r\n}",functionS);
    }

    public TemplateFileClassInfo(String templateClassPrefix, String templateClassSuffix, Map<String, String> functionS) {
        this.templateClassPrefix = templateClassPrefix;
        this.templateClassSuffix = templateClassSuffix;
        this.functionS = functionS;
    }

    public String getTemplateClassPrefix() {
        return templateClassPrefix;
    }

    public void setTemplateClassPrefix(String templateClassPrefix) {
        this.templateClassPrefix = templateClassPrefix;
    }

    public String getTemplateClassSuffix() {
        return templateClassSuffix;
    }

    public void setTemplateClassSuffix(String templateClassSuffix) {
        this.templateClassSuffix = templateClassSuffix;
    }

    public Map<String, String> getFunctionS() {
        return functionS;
    }

    public void setFunctionS(Map<String, String> functionS) {
        this.functionS = functionS;
    }

    @Override
    public String toString() {
        return "TemplateFileClassInfo{" +
                "templateClassPrefix='" + templateClassPrefix + '\'' +
                ", templateClassSuffix='" + templateClassSuffix + '\'' +
                ", functionS=" + functionS +
                '}';
    }
}
