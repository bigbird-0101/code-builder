package io.github.bigbird0101.code.core.factory.config;

/**
 * @author bigbird-0101
 */
public interface MultipleTemplateDefinitionRegistry {
    /**
     * 注册组合模板定义
     *
     * @param multipleTemplateDefinitionName 组合模板定义名称
     * @param multipleTemplateDefinition     multipleTemplateDefinition
     */
    void registerMultipleTemplateDefinition(String multipleTemplateDefinitionName, MultipleTemplateDefinition multipleTemplateDefinition);

    /**
     * 获取组合模板定义
     * @param multipleTemplateDefinitionName 组合模板定义名称
     * @return multipleTemplateDefinition
     */
    MultipleTemplateDefinition getMultipleTemplateDefinition(String multipleTemplateDefinitionName);
}
