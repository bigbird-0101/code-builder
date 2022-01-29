package com.fpp.code.core.factory;

import com.fpp.code.core.exception.CodeConfigException;

import java.io.IOException;

/**
 *
 * @author Administrator
 */
public interface ConfigurableListableTemplateFactory extends ConfigurableTemplateFactory{
    /**
     * 初始化模板
     * @throws CodeConfigException
     */
    void preInstantiateTemplates() throws CodeConfigException, IOException;

}
