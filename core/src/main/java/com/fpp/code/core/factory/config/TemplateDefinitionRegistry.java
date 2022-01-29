package com.fpp.code.core.factory.config;

import com.fpp.code.core.exception.CodeConfigException;

public interface TemplateDefinitionRegistry {
    void registerTemplateDefinition(String templateName, TemplateDefinition templateDefinition);
    TemplateDefinition getTemplateDefinition(String templateName) throws CodeConfigException;
}