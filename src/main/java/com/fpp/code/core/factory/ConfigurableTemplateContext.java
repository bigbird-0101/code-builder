package com.fpp.code.core.factory;

import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.context.TemplateContext;

import java.io.IOException;

/**
 * 可配置模板容器
 * @author Administrator
 */
public interface ConfigurableTemplateContext extends TemplateContext {

    /**
     * 设置环境
     * @param environment
     */
    void setEnvironment(Environment environment);

    /**
     * 刷新 容器
     * @throws CodeConfigException
     */
    void refresh() throws CodeConfigException, IOException;
}
