package com.fpp.code.core.template;

/**
 * 模板交易容器 获取当前的模板
 * @author Administrator
 */
public class TemplateTraceContext {

    private static final ThreadLocal<Template> CURRENT_TEMPLATE=new ThreadLocal<>();

    public static Template getCurrentTemplate() {
        return CURRENT_TEMPLATE.get();
    }

    public static void setCurrentTemplate(Template currentTemplate) {
        TemplateTraceContext.CURRENT_TEMPLATE.set(currentTemplate);
    }

    public static void clear(){
        CURRENT_TEMPLATE.remove();
    }
}
