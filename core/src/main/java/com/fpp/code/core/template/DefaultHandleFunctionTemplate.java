package com.fpp.code.core.template;

import com.alibaba.fastjson.annotation.JSONType;
import com.fpp.code.core.exception.CodeConfigException;

import java.io.IOException;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 18:24
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DefaultHandleFunctionTemplate extends AbstractHandleFunctionTemplate {

    public DefaultHandleFunctionTemplate(){
        super(null);
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
