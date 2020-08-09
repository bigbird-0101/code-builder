package main.java.template;

import java.util.Map;

/**
 * 模板语言解析器基类
 *
 * @author fpp
 * @version 1.0
 * @date 2020/6/9 16:43
 */
public interface TemplateLangResolver extends Rule {
    /**
     * 模板语言解析方法
     *
     * @param srcData         需要解析的模板数据
     * @param replaceKeyValue 模板中的变量数据
     * @return 解析后的字符串
     * @throws TemplateResolveException 模板解析异常
     */
    String langResolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException;
}
