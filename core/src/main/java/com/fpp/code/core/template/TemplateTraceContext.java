package com.fpp.code.core.template;

public class TemplateTraceContext {

    private static Template currentTemplate;

    public static Template getCurrentTemplate() {
        return currentTemplate;
    }

    public static void setCurrentTemplate(Template currentTemplate) {
        TemplateTraceContext.currentTemplate = currentTemplate;
    }
}
