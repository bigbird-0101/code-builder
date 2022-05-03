package com.fpp.code.core.filebuilder.definedfunction;

/**
 * 多个字段的替换规则
 * @author Administrator
 */
public abstract class AbstractMultipleRepresentFactorReplaceRule extends AbstractRepresentFactorReplaceRule{
    @Override
    public boolean match(String pendingString, String representFactor, String replaceString, String before, String after) {
        return isMultipleReplaced(replaceString)&&doMatch(pendingString,representFactor,before,after);
    }

    /**
     * 子类的匹配条件
     * @param pendingString
     * @param representFactor
     * @param before
     * @param after
     * @return
     */
    protected abstract boolean doMatch(String pendingString, String representFactor, String before, String after);

}
