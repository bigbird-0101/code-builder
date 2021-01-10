package com.fpp.code.core.template;

import com.alibaba.fastjson.annotation.JSONType;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.Environment;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 18:24
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DefaultHandleFunctionTemplate extends AbstractHandleFunctionTemplate {

    public DefaultHandleFunctionTemplate() throws CodeConfigException {
        super(null);
    }

    public DefaultHandleFunctionTemplate(String templateName,String templeFileName,String path) throws CodeConfigException {
        super(templeFileName);
        this.setTemplateName(templateName);
        this.setPath(path);
    }

    public DefaultHandleFunctionTemplate(String templateName,String templeFileName,ResolverStrategy resolverStrategy,String path) throws CodeConfigException {
        super(templeFileName);
        this.setTemplateName(templateName);
        this.setResolverStrategy(resolverStrategy);
        this.setPath(path);
    }

    public DefaultHandleFunctionTemplate(String templateName, String templeFileName, Environment environment, String path) throws CodeConfigException {
        super(templeFileName, environment);
        this.setTemplateName(templateName);
        this.setPath(path);
    }


    /**
     * 设置模板解析策略
     *
     * @param resolverStrategy 模板解析策略
     */
    @Override
    public void setResolverStrategy(ResolverStrategy resolverStrategy) {
        this.resolverStrategy=resolverStrategy;
    }
}
