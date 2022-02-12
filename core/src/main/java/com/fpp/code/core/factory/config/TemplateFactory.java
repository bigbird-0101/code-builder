package com.fpp.code.core.factory.config;

import com.fpp.code.core.template.MultipleTemplate;
import com.fpp.code.core.template.Template;

/**
 * 模板工厂
 * @author fpp
 */
public interface TemplateFactory {
    /**
     * 获取模板
     * @param templateName
     * @return
     */
    Template getTemplate(String templateName);

    /**
     * 获取模板
     * @param templateName
     * @return
     */
    MultipleTemplate getMultipleTemplate(String templateName);
}
