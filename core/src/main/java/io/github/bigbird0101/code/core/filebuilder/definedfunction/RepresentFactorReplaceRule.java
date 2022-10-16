package io.github.bigbird0101.code.core.filebuilder.definedfunction;

/**
 * @author Administrator
 */
public interface RepresentFactorReplaceRule {
    /**
     * 是否解析匹配替换策略
     * @param pendingString 源字符串
     * @param before 负载因子的前面的第一个字符
     * @param replaceString 需要被替换的字段集
     * @param representFactor 负载因子
     * @param after 负载因子的后面的第一个字符
     * @return
     */
    boolean match(String pendingString, String representFactor, String replaceString, String before,String after);

    /**
     * 根据代表因子和 替换 string 替换 functionBody中的代表因子
     * @param pendingString 源字符串
     * @param before 负载因子的前面的第一个字符
     * @param replaceString 需要被替换的字段集
     * @param representFactor 负载因子
     * @param after 负载因子的后面的第一个字符
     * @return
     */
    String replace(String pendingString, String representFactor, String replaceString, String before,String after);
}
