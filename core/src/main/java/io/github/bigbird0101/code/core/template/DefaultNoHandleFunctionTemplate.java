package io.github.bigbird0101.code.core.template;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author fpp
 * @version 1.0
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DefaultNoHandleFunctionTemplate extends AbstractNoHandleFunctionTemplate {
}
