package com.fpp.code.core.template;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 18:24
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DefaultHandleFunctionTemplate extends AbstractHandleFunctionTemplate {

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
