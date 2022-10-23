package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.ReUtil;

/**
 * 单个需要替换的字段的实现
 * @author Administrator
 */
public class SingletonReplaceStringRepresentFactorReplaceRule extends AbstractRepresentFactorReplaceRule {
    @Override
    public boolean match(String pendingString, String representFactor, String replaceString, String before, String after) {
        return !isMultipleReplaced(replaceString);
    }

    @Override
    public String replace(String pendingString, String representFactor, String replaceString, String before, String after) {
        return pendingString.replaceFirst(ReUtil.escape(before + representFactor + after),
                ReUtil.escape(before + templateStringByRepresentFactor(representFactor, replaceString) +after));
    }
}
