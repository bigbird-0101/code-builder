package com.fpp.code.factory;

import java.util.Set;

public interface TemplateScanner{
    Set<TemplateDefinitionHolder> scanner(String templatesFilePath);
}
