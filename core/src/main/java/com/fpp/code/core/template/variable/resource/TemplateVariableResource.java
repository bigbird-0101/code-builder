package com.fpp.code.core.template.variable.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板变量资源
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-24 23:11:48
 */
public interface TemplateVariableResource {
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
                .map(TemplateVariableResource::getTemplateVariable)
                .forEach(result::putAll);
        return result;
    }
}
