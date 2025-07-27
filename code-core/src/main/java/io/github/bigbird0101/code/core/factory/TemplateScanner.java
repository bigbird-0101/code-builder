package io.github.bigbird0101.code.core.factory;

import io.github.bigbird0101.code.core.exception.CodeConfigException;

import java.io.Serializable;

/**
 * 所有模板类型扫描器
 * @author Administrator
 */
public interface TemplateScanner extends Serializable {
    /**
     * 扫描获取所有的类型模板定义
     * @param templatesPath 模板文件路径
     * @param templateConfigPath 模板配置文件路径
     * @return 所有的类型模板定义
     * @throws CodeConfigException 配置文件异常
     */
    AllTemplateDefinitionHolder scanner(String templatesPath, String templateConfigPath) throws CodeConfigException;
}
