package com.fpp.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.fpp.code.core.domain.DefinedFunctionDomain;
import com.fpp.code.util.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.lang.Validator.isChinese;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 9:03
 */
public interface DefinedFunctionResolverRule {
    String QUOTATION_MARKS = "\"";
    List<String> SPECIAL_CHAR = Arrays.asList("\"", "=", "(", ")", ",", "'"," ", "[", "]", "+", "-", "*", "&", "^", "%", "$", "#", "@", "~", "`", "<", ">", ".", "\\", ";", ":");
    String COMMA = ",";
    String PARAM = "param";
    String PARAM_PREFIX = "paramPrefix";
    String PARAM_SUFFIX = "paramSuffix";

    /**
     * 将模板方法根据规则解析成自定义方法
     * @param definedFunctionDomain
     * @return 解析后自定义方法
     */
    String doRule(DefinedFunctionDomain definedFunctionDomain);

    /**
     * 替换方法体当中的替换因子 用新的替换字符串来替换
     * @param functionBody
     * @param replaceString
     * @param representFactor
     * @return
     */
    default String replace(String functionBody, String representFactor, String replaceString) {
        int representFactorIndex=0;
        int oldRepresentFactorIndex=representFactorIndex;
        while (
                (
                  (representFactorIndex = functionBody.indexOf(representFactor)) > 0
                   &&(representFactorIndex!=oldRepresentFactorIndex)
                )
                ||functionBody.startsWith(representFactor)
        ) {
            oldRepresentFactorIndex=representFactorIndex;
            String before;
            try {
                before = functionBody.substring(representFactorIndex - 1, representFactorIndex);
            }catch (Exception e){
                before=StrUtil.EMPTY;
            }
            String after;
            try {
                after = functionBody.substring(representFactorIndex + representFactor.length(), representFactorIndex + representFactor.length() + 1);
            }catch (Exception e){
                after=StrUtil.EMPTY;
            }
            //前后都是特殊字符或者都是中文
            if (isSpecialCharOrChinese(before, after)) {
                if (replaceString.contains(COMMA)) {
                    if(COMMA.equals(before)){
                        functionBody = functionBody.replaceFirst(StrUtil.BACKSLASH + COMMA + representFactor + StrUtil.BACKSLASH + after, StrUtil.BACKSLASH + before + templateStringByRepresentFactor(representFactor, replaceString) + StrUtil.BACKSLASH + after);
                    }else if(isChinese(before)&&isChinese(after)){
                        functionBody = functionBody.replaceFirst(before + representFactor  + after, before + templateStringByRepresentFactor(representFactor, replaceString)+ after);
                    }else{
                        Pattern compile = Pattern.compile(before + representFactor + "(?<param>.*?)\\{\\s*\\}", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
                        Matcher matcher = compile.matcher(functionBody);
                        while(matcher.find()){
                            final String param = matcher.group(PARAM);
                            if(QUOTATION_MARKS.equals(before)){
                                final String collect = Stream.of(replaceString.split(COMMA)).map(s -> templateStringByRepresentFactor(representFactor, s)).map(s ->  s + param + StrUtil.EMPTY_JSON).collect(Collectors.joining(" "));
                                functionBody = functionBody.replaceFirst(StrUtil.BACKSLASH + before + representFactor+param+ "\\{\\s*\\}", StrUtil.BACKSLASH + before+collect);
                            }else{
                                String finalBefore = before;
                                final String collect = Stream.of(replaceString.split(COMMA)).map(s -> templateStringByRepresentFactor(representFactor, s)).map(s -> StrUtil.BACKSLASH + finalBefore + s + param + "\\{\\s*\\}").collect(Collectors.joining(" "));
                                functionBody = functionBody.replaceFirst(StrUtil.BACKSLASH + before + representFactor+param+ "\\{\\s*\\}", collect);
                            }
                        }

                        compile = Pattern.compile("(?<paramPrefix>.*?)@param"+before + representFactor + after+"(?<paramSuffix>.*)", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
                        matcher = compile.matcher(functionBody);
                        while(matcher.find()){
                            final String paramSuffix = matcher.group(PARAM_SUFFIX);
                            final String paramPrefix = matcher.group(PARAM_PREFIX);
                            String finalAfter = after;
                            String finalBefore = before;
                            final String collect = Stream.of(replaceString.split(COMMA)).map(s -> templateStringByRepresentFactor(representFactor, s)).map(s ->  paramPrefix+"@param"+ finalBefore + s+ finalAfter + replace(paramSuffix,representFactor,s)).collect(Collectors.joining("\r\n"));
                            functionBody = functionBody.replaceFirst(ReUtil.escape(paramPrefix+"@param"+before + representFactor + after+paramSuffix), ReUtil.escape(collect));
                        }
                    }
                } else {
                    functionBody = functionBody.replaceFirst(ReUtil.escape(before + representFactor + after), ReUtil.escape(before + templateStringByRepresentFactor(representFactor, replaceString) +after));
                }
            }else{
                break;
            }
        }
        return functionBody;
    }

    /**
     * 前后都是特殊字符或者是中文
     * @param before
     * @param after
     * @return
     */
    default boolean isSpecialCharOrChinese(String before, String after) {
        return (
                        SPECIAL_CHAR.contains(before) && SPECIAL_CHAR.contains(after))
                        ||
                        (isChinese(before)&& isChinese(after)
                        ||
                        (isChinese(before)&&StrUtil.EMPTY.equals(after))
                        ||
                        (isChinese(after)&&StrUtil.EMPTY.equals(before))
                        ||
                        (StrUtil.EMPTY.equals(after)&&StrUtil.EMPTY.equals(before))
                );
    }

    /**
     * 根据替换因子格式 获取替换因子相同格式的替换字符串
     * @param representFactor
     * @param replaceString
     * @return
     */
    default String templateStringByRepresentFactor(String representFactor, String replaceString) {
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
}
