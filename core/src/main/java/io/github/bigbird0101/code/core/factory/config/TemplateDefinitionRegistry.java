package io.github.bigbird0101.code.core.factory.config;

import io.github.bigbird0101.code.core.exception.CodeConfigException;

public interface TemplateDefinitionRegistry {
    void registerTemplateDefinition(String templateName, TemplateDefinition templateDefinition);
    TemplateDefinition getTemplateDefinition(String templateName) throws CodeConfigException;
}