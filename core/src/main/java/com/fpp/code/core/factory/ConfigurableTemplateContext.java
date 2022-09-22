package com.fpp.code.core.factory;

import com.fpp.code.core.context.TemplateContext;
import com.fpp.code.core.event.BasicCodeListener;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.factory.config.TemplateFactoryPostProcessor;

/**
 * 可配置模板容器
 * @author Administrator
 */
public interface ConfigurableTemplateContext extends TemplateContext {

    /**
     * 刷新 容器
     * @throws CodeConfigException
     */
    void refresh();

    /**
     * 添加TemplateFactoryPostProcessor
     * @param templateFactoryPostProcessor
     */
    void addTemplateFactoryPostProcessor(TemplateFactoryPostProcessor templateFactoryPostProcessor);

    /**
     * 添加基础代码监听
     * @param listener
     */
    void addBasicCodeListener(BasicCodeListener<?> listener);
}
