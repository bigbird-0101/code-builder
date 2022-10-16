package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import static cn.hutool.core.lang.Validator.isChinese;

/**
 * 特殊字符的负载因子替换规则
 * @author Administrator
 */
public class SpecialCharMultipleRepresentFactorReplaceRule extends AbstractMultipleRepresentFactorReplaceRule{
    @Override
    public boolean doMatch(String pendingString, String representFactor, String before, String after) {
        return (COMMA.equals(before)||(isChinese(before)||isChinese(after)));
    }

    @Override
    public String replace(String pendingString, String representFactor, String replaceString, String before, String after) {
        if(COMMA.equals(before)){
            pendingString = pendingString.replaceFirst(StrUtil.BACKSLASH + COMMA + representFactor + StrUtil.BACKSLASH + after, StrUtil.BACKSLASH + before + templateStringByRepresentFactor(representFactor, replaceString) + StrUtil.BACKSLASH + after);
        }else if(isChinese(before)||isChinese(after)){
            pendingString = pendingString.replaceFirst(ReUtil.escape(before + representFactor  + after), ReUtil.escape(before + templateStringByRepresentFactor(representFactor, replaceString)+ after));
        }
        return pendingString;
    }
}
