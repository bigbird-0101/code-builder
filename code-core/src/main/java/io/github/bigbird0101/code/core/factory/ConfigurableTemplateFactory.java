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
     * @return 模板名集合
     */
    Set<String> getTemplateNames();

    /**
     * 获取组合模板名集合
     * @return 组合模板名集合
     */
    Set<String> getMultipleTemplateNames();

    /**
     * 添加拦截器
     * @param templatePostProcessor 模板处理器
     */
    void addTemplatePostProcessor(TemplatePostProcessor templatePostProcessor);

    /**
     * 设置环境
     * @param environment 环境对象
     */
    void setEnvironment(Environment environment);

    /**
     * 销毁模板
     */
    void destroyTemplates();
}
