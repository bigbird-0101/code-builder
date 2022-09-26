package com.fpp.code.core.template.variable.resource;

import java.util.*;

/**
 * 模板变量资源
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-24 23:11:48
 */
public interface TemplateVariableResource {
    String DEFAULT_SRC_RESOURCE_KEY="SrcSourceName";
    String DEFAULT_SRC_RESOURCE_VALUE="TempTemplate";
    /**
     * 获取模板变量资源
     * @return
     */
    Map<String,Object> getTemplateVariable();

    /**
     * 合并多个模板变量
     * @param templateVariableResources
     * @return
     */
    default Map<String,Object> mergeTemplateVariable(List<TemplateVariableResource> templateVariableResources){
        Map<String,Object> result=new HashMap<>();
        templateVariableResources.stream()
                .filter(Objects::nonNull)
                .map(TemplateVariableResource::getTemplateVariable)
                .forEach(result::putAll);
        return result;
    }

    /**
     * 合并多个模板变量
     * @param templateVariableResources
     * @return
     */
    default Map<String,Object> mergeTemplateVariable(TemplateVariableResource... templateVariableResources){
        return mergeTemplateVariable(Arrays.asList(templateVariableResources));
    }
}
