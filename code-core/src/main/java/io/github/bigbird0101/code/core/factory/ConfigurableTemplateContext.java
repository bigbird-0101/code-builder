package io.github.bigbird0101.code.core.factory;

import io.github.bigbird0101.code.core.context.TemplateContext;
import io.github.bigbird0101.code.core.event.BasicCodeListener;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.factory.config.TemplateFactoryPostProcessor;

/**
 * 可配置模板容器
 * @author Administrator
 */
public interface ConfigurableTemplateContext extends TemplateContext {

    /**
     * 刷新 容器
     * @throws CodeConfigException CodeConfigException
     */
    void refresh();

    /**
     * 添加TemplateFactoryPostProcessor
     * @param templateFactoryPostProcessor templateFactoryPostProcessor
     */
    void addTemplateFactoryPostProcessor(TemplateFactoryPostProcessor templateFactoryPostProcessor);

    /**
     * 添加基础代码监听
     * @param listener  listener
     */
    void addBasicCodeListener(BasicCodeListener<?> listener);
}
