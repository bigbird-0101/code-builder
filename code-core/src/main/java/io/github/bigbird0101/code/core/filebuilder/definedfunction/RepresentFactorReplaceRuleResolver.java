package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import java.util.Map;

/**
 * @author Administrator
 */
public interface RepresentFactorReplaceRuleResolver {
    /**
     * 根据代表因子和 替换 string 替换 pendingString 中的代表因子
     *
     * @param dataModel
     * @param pendingString
     * @param representFactor
     * @param replaceString
     * @return
     */
    String doResolver(Map<String, Object> dataModel, String pendingString, String representFactor, String replaceString);

    /**
     * 添加解析规则
     * @param representFactorReplaceRule 自定义方法解析器的解析规则
     */
    void addResolverRule(RepresentFactorReplaceRule representFactorReplaceRule);
}
