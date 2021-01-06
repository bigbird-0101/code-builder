package com.fpp.code.factory.config;

public interface TemplateDefinitionRegistry {
    void registerTemplateDefinition(String templateName, TemplateDefinition templateDefinition);
    TemplateDefinition getTemplateDefinition(String templateName);
}