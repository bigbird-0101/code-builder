package com.fpp.code.core.template;

import java.util.Map;
import java.util.Set;

/**
 * 多个模板集合 组合
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 18:19
 */
public interface MultipleTemplate {
    /**
     * 获取模板名称
     * @return
     */
    String getTemplateName();

    /**
     * 设置模板名称
     * @param templateName
     */
    void setTemplateName(String templateName);
    /**
     * 获取多个模板集合
     * @return
     */
    Set<Template> getTemplates();

    /**
     * 设置模板集合
     * @param templates
     */
    void setTemplates(Set<Template> templates);

    /**
     * 设置模板解析策略
     * @param templateResolverStrategy 模板解析策略  k-为模板名 键值为模板解析策略
     */
    default void setResolverStrategys(Map<String,ResolverStrategy> templateResolverStrategy){

    }
}
