package io.github.bigbird0101.code.core.factory;

import io.github.bigbird0101.code.core.exception.CodeConfigException;

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
