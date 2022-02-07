package com.fpp.code.core.factory;

import com.fpp.code.core.config.Environment;
import com.fpp.code.core.factory.config.EnvironmentCapable;
import com.fpp.code.core.factory.config.TemplateFactory;
import com.fpp.code.core.factory.config.TemplatePostProcessor;

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

    void addPostProcessor(TemplatePostProcessor templatePostProcessor);

    /**
     * 设置环境
     * @param environment
     */
    void setEnvironment(Environment environment);
}
