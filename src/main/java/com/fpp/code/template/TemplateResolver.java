package com.fpp.code.template;

import java.util.Map;

/**
 * 模板解析器
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 11:11
 */
public interface TemplateResolver extends TemplateVariableResolver{
    /**
     * 模板语言解析方法
     *
     * @param srcData         需要解析的模板数据
     * @param replaceKeyValue 模板中的变量数据
     * @return 解析后的字符串
     * @throws IllegalAccessException 解析其中的对象字段异常
     * @throws NoSuchFieldException   解析其中的对象字段异常
     */
    String resolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException;
}
