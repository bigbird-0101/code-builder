package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.exception.TemplateResolveException;

import java.util.Map;

/**
 * 模板解析器
 * @author fpp
 * @version 1.0
 */
public interface TemplateResolver extends TemplateVariableResolver{
    /**
     * 模板语言解析方法
     *
     * @param srcData         需要解析的模板数据
     * @param replaceKeyValue 模板中的变量数据
     * @return 解析后的字符串
     * @throws TemplateResolveException 模板解析异常
     */
    String resolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException;
}
