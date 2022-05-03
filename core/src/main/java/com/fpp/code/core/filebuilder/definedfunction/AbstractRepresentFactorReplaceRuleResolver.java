package com.fpp.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.hutool.core.lang.Validator.isChinese;

/**
 * @author Administrator
 */
public abstract class AbstractRepresentFactorReplaceRuleResolver implements RepresentFactorReplaceRuleResolver{
    protected List<RepresentFactorReplaceRule> ruleList=new ArrayList<>(10);

    private static final List<String> SPECIAL_CHAR =
            Arrays.asList("\"", "=", "(", ")", ",", "'"," ", "[", "]", "+", "-", "*", "&", "^", "%", "$", "#", "@",
                    "~", "`", "<", ">", ".", "\\", ";", ":","");

    @Override
    public String doResolver(String pendingString, String representFactor, String replaceString) {
        int representFactorIndex=0;
        int oldRepresentFactorIndex=representFactorIndex;
        while (
                (
                        (representFactorIndex = pendingString.indexOf(representFactor)) > 0
                                &&(representFactorIndex!=oldRepresentFactorIndex)
                )
                        ||pendingString.startsWith(representFactor)
        ) {
            oldRepresentFactorIndex = representFactorIndex;
            String before = getBefore(pendingString, representFactorIndex);
            String after = getAfter(pendingString, representFactor, representFactorIndex);
            //前后都是特殊字符或者都是中文
            if (isSpecialCharOrChinese(before, after)) {
                for (RepresentFactorReplaceRule representFactorReplaceRule:ruleList){
                    if(representFactorReplaceRule.match(pendingString,representFactor,replaceString,before,after)){
                        pendingString=representFactorReplaceRule.replace(pendingString,representFactor,
                                replaceString,before,after);
                    }
                }
            }else{
                break;
            }
        }
        return pendingString;
    }

    @Override
    public void addResolverRule(RepresentFactorReplaceRule representFactorReplaceRule) {
        this.ruleList.add(representFactorReplaceRule);
    }

    protected String getAfter(String pendingString, String representFactor, int representFactorIndex) {
        String after;
        try {
            after = pendingString.substring(representFactorIndex + representFactor.length(), representFactorIndex + representFactor.length() + 1);
        }catch (Exception e){
            after= StrUtil.EMPTY;
        }
        return after;
    }

    protected String getBefore(String pendingString, int representFactorIndex) {
        String before;
        try {
            before = pendingString.substring(representFactorIndex - 1, representFactorIndex);
        }catch (Exception e){
            before= StrUtil.EMPTY;
        }
        return before;
    }

    /**
     * 前后都是特殊字符或者是中文
     * @param before
     * @param after
     * @return
     */
    protected boolean isSpecialCharOrChinese(String before, String after) {
        return (
                (SPECIAL_CHAR.contains(before) && SPECIAL_CHAR.contains(after))
                        ||
                        (isChinese(before)&& isChinese(after))
                        ||
                        (isChinese(before)&& SPECIAL_CHAR.contains(after))
                        ||
                        (SPECIAL_CHAR.contains(before) && isChinese(after))
                        ||
                        (isChinese(before)&& StrUtil.EMPTY.equals(after))
                        ||
                        (isChinese(after)&&StrUtil.EMPTY.equals(before))
        );
    }

}
