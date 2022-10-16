package io.github.bigbird0101.code.core.factory;

import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.factory.config.EnvironmentCapable;
import io.github.bigbird0101.code.core.factory.config.TemplateFactory;
import io.github.bigbird0101.code.core.factory.config.TemplatePostProcessor;

import java.util.Set;

/**
 * @author Administrator
 */
public interface ConfigurableTemplateFactory extends TemplateFactory,EnvironmentCapable {


    /**
     * 获取模板名集合
     * @return
     */
    Set<String> getTemplateNames();

    /**
     * 获取组合模板名集合
     * @return
     */
    Set<String> getMultipleTemplateNames();

    /**
     * 添加拦截器
     * @param templatePostProcessor
     */
    void addTemplatePostProcessor(TemplatePostProcessor templatePostProcessor);

    /**
     * 设置环境
     * @param environment
     */
    void setEnvironment(Environment environment);

    /**
     * 销毁模板
     */
    void destroyTemplates();
}
