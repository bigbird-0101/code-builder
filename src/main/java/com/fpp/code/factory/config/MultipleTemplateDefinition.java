package com.fpp.code.factory.config;

import java.util.List;

public interface MultipleTemplateDefinition {
    List<TemplateDefinition> getTemplateDefinitions();
    boolean containsTemplateDefinition(String templateName);
}
