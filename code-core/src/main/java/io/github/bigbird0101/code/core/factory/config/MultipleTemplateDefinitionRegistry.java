package io.github.bigbird0101.code.core.factory.config;

public interface MultipleTemplateDefinitionRegistry {
    void registerMultipleTemplateDefinition(String multipleTemplateDefinitionName, MultipleTemplateDefinition multipleTemplateDefinition);
    MultipleTemplateDefinition getMultipleTemplateDefinition(String multipleTemplateDefinitionName);
}
