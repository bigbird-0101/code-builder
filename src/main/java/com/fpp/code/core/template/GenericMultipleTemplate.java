package com.fpp.code.core.template;

import com.alibaba.fastjson.annotation.JSONType;

import java.util.Map;
import java.util.Set;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/1/7 14:03
 */
@JSONType(serializer = AbstractMultipleTemplate.MultipleTemplateSerializer.class)
public class GenericMultipleTemplate extends AbstractMultipleTemplate{
    private String templateName;
    private Set<Template> templates;

    @Override
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public Set<Template> getTemplates() {
        return templates;
    }

    @Override
    public void setTemplates(Set<Template> templates) {
        this.templates = templates;
    }

    /**
     * 获取模板名称
     *
     * @return
     */
    @Override
    public String getTemplateName() {
        return templateName;
    }

    /**
     * 设置模板解析策略
     *
     * @param templateResolverStrategy 模板解析策略  k-为模板名 键值为模板解析策略
     */
    @Override
    public void setResolverStrategys(Map<String, ResolverStrategy> templateResolverStrategy) {
        this.templates.forEach(item -> {
            if (item instanceof AbstractHandleFunctionTemplate && templateResolverStrategy.containsKey(item.getTemplateName())) {
                ((AbstractHandleFunctionTemplate) item).setResolverStrategy(templateResolverStrategy.get(item.getTemplateName()));
            }
        });
    }

}
