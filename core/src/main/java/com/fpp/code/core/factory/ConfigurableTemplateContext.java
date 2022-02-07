package com.fpp.code.core.factory;

import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.context.TemplateContext;
import com.fpp.code.core.factory.config.TemplateFactoryPostProcessor;

import java.io.IOException;

/**
 * 可配置模板容器
 * @author Administrator
 */
public interface ConfigurableTemplateContext extends TemplateContext {

    /**
     * 刷新 容器
     * @throws CodeConfigException
     */
    void refresh() throws CodeConfigException, IOException;

    void addTemplateFactoryPostProcessor(TemplateFactoryPostProcessor templateFactoryPostProcessor);
}
