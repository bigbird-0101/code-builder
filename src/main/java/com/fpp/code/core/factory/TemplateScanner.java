package com.fpp.code.core.factory;

import com.fpp.code.core.config.CodeConfigException;

/**
 * 所有模板类型扫描器
 * @author Administrator
 */
public interface TemplateScanner {
    /**
     * 扫描获取所有的类型模板定义
     * @param templatesPath 模板文件路径
     * @param templateConfigPath 模板配置文件路径
     * @return 所有的类型模板定义
     */
    AllTemplateDefinitionHolder scanner(String templatesPath,String templateConfigPath) throws CodeConfigException;
}
