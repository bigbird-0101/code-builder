package com.fpp.code.template;

import java.util.Map;
import java.util.Set;

/**
 * 模板变量解析器
 *
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 11:43
 */
public interface TemplateVariableResolver{
    /**
     * 获取字符串中模板变量中的值
     *
     * @param srcStr
     * @return
     */
    Set<String> getTemplateVariableKey(String srcStr);

    /**
     * 替换掉 replaceTargetS 中的所有 在replaceKeyValue 能够找到的记录
     *
     * @param replaceKeyValue 存储的替换表
     * @param replaceTargetS  替换目标
     * @return 替换掉的最终结果
     */
    String replace(Map<String, Object> replaceKeyValue, String replaceTargetS);

    /**
     * 解析含有模板变量的字符串
     *
     * @param variableSet     模板变量
     * @param targetObjectKey 模板变量的中需要使用的对象的key
     * @param targetObject    模板变量的中需要使用的对象
     * @param body            含有模板变量的字符串
     * @return
     * @throws TemplateResolveException 模板解析异常
     */
    String analysisBody(Set<String> variableSet, String targetObjectKey, Object targetObject, String body) throws TemplateResolveException;
}
