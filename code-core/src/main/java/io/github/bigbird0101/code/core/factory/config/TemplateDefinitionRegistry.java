package io.github.bigbird0101.code.core.factory.config;

import io.github.bigbird0101.code.core.exception.CodeConfigException;

/**
 * 模板定义注册中心
 *
 * @author bigbird-0101
 */
public interface TemplateDefinitionRegistry {
    /**
     * 注册模板定义
     * @param templateName 模板名称
     * @param templateDefinition 模板定义
     */
    void registerTemplateDefinition(String templateName, TemplateDefinition templateDefinition);

    /**
     * 获取模板定义
     * @param templateName 模板名称
     * @return 模板定义
     * @throws CodeConfigException 配置异常
     */
    TemplateDefinition getTemplateDefinition(String templateName) throws CodeConfigException;
}