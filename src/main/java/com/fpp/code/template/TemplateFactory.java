package com.fpp.code.template;

import com.fpp.code.config.CodeConfigException;

import java.io.IOException;
import java.util.List;

/**
 * 模板工厂
 */
public interface TemplateFactory {

    /**
     * 根据模板配置获取模板
     * @return 模板
     */
    List<Template> getTemplates() throws IOException, CodeConfigException;
}
