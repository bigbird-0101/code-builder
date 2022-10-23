package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.util.Utils;

/**
 * @author Administrator
 */
public abstract class AbstractRepresentFactorReplaceRule implements RepresentFactorReplaceRule {
    protected static final String COMMA = ",";

    private RepresentFactorReplaceRuleResolver representFactorReplaceRuleResolver;

    public AbstractRepresentFactorReplaceRule() {
    }

    public AbstractRepresentFactorReplaceRule(RepresentFactorReplaceRuleResolver representFactorReplaceRuleResolver) {
        this.representFactorReplaceRuleResolver = representFactorReplaceRuleResolver;
    }

    public RepresentFactorReplaceRuleResolver getRepresentFactorReplaceRuleResolver() {
        return representFactorReplaceRuleResolver;
    }

    public void setRepresentFactorReplaceRuleResolver(RepresentFactorReplaceRuleResolver representFactorReplaceRuleResolver) {
        this.representFactorReplaceRuleResolver = representFactorReplaceRuleResolver;
    }

    /**
     * 根据替换因子格式 获取替换因子相同格式的替换字符串
     * @param representFactor
     * @param replaceString
     * @return
     */
    protected String templateStringByRepresentFactor(String representFactor, String replaceString) {
        if (Utils.isUpper(representFactor)) {
            return replaceString.toUpperCase();
        } else if (Utils.isLower(representFactor)) {
            return replaceString.toLowerCase();
        } else if (StrUtil.toCamelCase(representFactor).equals(representFactor)) {
            return StrUtil.toCamelCase(replaceString);
        } else if (StrUtil.toUnderlineCase(representFactor).equals(representFactor)) {
            return StrUtil.toUnderlineCase(replaceString);
        } else {
            return replaceString;
        }
    }

    /**
     * 是否是多个被替换
     * @param replaceString
     * @return
     */
    protected boolean isMultipleReplaced(String replaceString){
        return replaceString.contains(COMMA);
    }
}
