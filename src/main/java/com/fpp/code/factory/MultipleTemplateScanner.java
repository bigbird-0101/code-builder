package com.fpp.code.factory;

import java.io.IOException;
import java.util.Set;

public interface MultipleTemplateScanner {
    Set<MultipleTemplateDefinitionHolder> scannerMultiple(String templatesFilePath) throws IOException;
}
