package com.fpp.code.core.template;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/5/19 11:14
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DefaultNoHandleFunctionTemplate extends AbstractNoHandleFunctionTemplate {

    public DefaultNoHandleFunctionTemplate() {
        super(null);
    }
}
