package com.fpp.code.core.template;

/**
 * 模板交易容器 获取当前的模板
 * @author Administrator
 */
public class TemplateTraceContext {

    private static ThreadLocal<Template> currentTemplate=new ThreadLocal<>();

    public static Template getCurrentTemplate() {
        return currentTemplate.get();
    }

    public static void setCurrentTemplate(Template currentTemplate) {
        TemplateTraceContext.currentTemplate.set(currentTemplate);
    }
}
