package com.fpp.code.core.template;

/**
 * 模板交易容器 获取当前的模板
 * @author Administrator
 */
public class TemplateTraceContext {

    private static Template currentTemplate;

    public static Template getCurrentTemplate() {
        return currentTemplate;
    }

    public static void setCurrentTemplate(Template currentTemplate) {
        TemplateTraceContext.currentTemplate = currentTemplate;
    }
}
