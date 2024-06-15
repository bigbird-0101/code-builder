package io.github.bigbird0101.code.core.template.variable.resource;

import io.github.bigbird0101.code.core.spi.TypeBasedSPI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * 模板变量资源
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-24 23:11:48
 */
public interface TemplateVariableResource extends TypeBasedSPI {
    String DEFAULT_SRC_RESOURCE_KEY="SrcSourceName";
    String DEFAULT_SRC_RESOURCE_VALUE="TempTemplate";
    String FILE_INPUT_STREAM = "configFilePathStream";

    /**
     * 获取模板变量资源
     * @return
     */
    Map<String,Object> getTemplateVariable();

    /**
     * 合并多个模板变量
     * @param templateVariableResources templateVariableResources
     * @return 模板变量
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
     * @param templateVariableResources templateVariableResources
     * @return 模板变量
     */
    default Map<String,Object> mergeTemplateVariable(TemplateVariableResource... templateVariableResources){
        return mergeTemplateVariable(Arrays.asList(templateVariableResources));
    }
}
