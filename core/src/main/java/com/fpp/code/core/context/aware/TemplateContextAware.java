package com.fpp.code.core.context.aware;

import com.fpp.code.core.context.TemplateContext;

/**
 * 模板容器感知接口
 * @author Administrator
 */
public interface TemplateContextAware {
    /**
     * 设置模板容器
     * @param templateContext
     */
    void setTemplateContext(TemplateContext templateContext);
}
