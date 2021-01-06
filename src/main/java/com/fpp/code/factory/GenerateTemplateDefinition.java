package com.fpp.code.factory;

import java.io.File;

public class GenerateTemplateDefinition extends AbstractTemplateDefinition {

    public GenerateTemplateDefinition(String projectUrl, String module, String sourcesRoot, String srcPackage, String templateName, String fileSuffixName, boolean isHandleFunction, File templateFile) {
        super(projectUrl, module, sourcesRoot, srcPackage, templateName, fileSuffixName, isHandleFunction, templateFile);
    }
}
