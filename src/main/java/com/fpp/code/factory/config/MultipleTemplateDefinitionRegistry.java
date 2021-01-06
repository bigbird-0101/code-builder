package com.fpp.code.factory.config;

public interface MultipleTemplateDefinitionRegistry {
    void registerMultipleTemplateDefinition(String multipleTemplateDefinitionName, MultipleTemplateDefinition multipleTemplateDefinition);
    MultipleTemplateDefinition getMultipleTemplateDefinition(String multipleTemplateDefinitionName);
}
