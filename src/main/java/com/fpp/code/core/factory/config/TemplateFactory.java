package com.fpp.code.core.factory.config;

import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.template.MultipleTemplate;
import com.fpp.code.core.template.Template;

import java.io.IOException;

/**
 * 模板工厂
 * @author fpp
 */
public interface TemplateFactory {
    /**
     * 获取模板
     * @param templateName
     * @return
     * @throws CodeConfigException
     */
    Template getTemplate(String templateName) throws CodeConfigException, IOException;

    /**
     * 获取模板
     * @param templateName
     * @return
     * @throws CodeConfigException
     */
    MultipleTemplate getMultipleTemplate(String templateName) throws CodeConfigException, IOException;
}
