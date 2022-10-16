package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.domain.DefinedFunctionDomain;
import io.github.bigbird0101.code.util.Utils;

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
    List<String> SPECIAL_CHAR = Arrays.asList("\"", "=", "(", ")", ",", "'"," ", "[", "]", "+", "-", "*", "&", "^", "%", "$", "#", "@", "~", "`", "<", ">", ".", "\\", ";", ":","");
    String COMMA = ",";
    String PARAM = "param";
    String PARAM_PREFIX = "paramPrefix";
    String PARAM_SUFFIX = "paramSuffix";

    String FUNCTION_BODY = "functionBody";
    String FUNCTION_BODY_OUTER = FUNCTION_BODY + "Outer";
    String FUNCTION_RETURN_VALUE = "functionReturnValue";
    String FUNCTION_NAME = "functionName";
    String FUNCTION_PARAM = "functionParam";
    String FUNCTION_THROW_ERROR = "functionThrowError";
    /**
     * 方法正则
     */
    Pattern FUNCTION = Pattern.compile("(?<" + FUNCTION_BODY_OUTER + ">.*)(?:private|public|protected)?\\s+(?<" + FUNCTION_RETURN_VALUE + ">.*?)\\s+(?<" + FUNCTION_NAME + ">.*?)\\s*\\(\\s*(?<" + FUNCTION_PARAM + ">.*)\\s*\\)\\s*(?<" + FUNCTION_THROW_ERROR + ">.*?)\\s*\\{\\s*(?<" + FUNCTION_BODY + ">(.*){2,5})\\s*\\}",
            Pattern.DOTALL|Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);
    /**
     * 接口方法正则
     */
    Pattern INTERFACE_FUNCTION = Pattern.compile("(?<" + FUNCTION_BODY_OUTER + ">.*)(?:public)?\\s+(?<" + FUNCTION_RETURN_VALUE + ">.*?)\\s+(?<" + FUNCTION_NAME + ">.*?)\\s*\\(\\s*(?<" + FUNCTION_PARAM + ">.*)\\s*\\)\\s*(?<" + FUNCTION_THROW_ERROR + ">.*?)\\s*;\\s*",
            Pattern.DOTALL|Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);
    /**
     * 将模板方法根据规则解析成自定义方法
     * @param definedFunctionDomain
     * @return 解析后自定义方法
     */
    String doRule(DefinedFunctionDomain definedFunctionDomain);

    /**
     * 替换方法体当中的替换因子 用新的替换字符串来替换
     * @param pendingString
     * @param replaceString
     * @param representFactor
     * @return
     */
    default String replace(String pendingString, String representFactor, String replaceString) {
        int representFactorIndex=0;
        int oldRepresentFactorIndex=representFactorIndex;
        while (
                (
                  (representFactorIndex = pendingString.indexOf(representFactor)) > 0
                   &&(representFactorIndex!=oldRepresentFactorIndex)
                )
                ||pendingString.startsWith(representFactor)
        ) {
            oldRepresentFactorIndex=representFactorIndex;
            String before = getBefore(pendingString, representFactorIndex);
            String after = getAfter(pendingString, representFactor, representFactorIndex);
            //前后都是特殊字符或者都是中文
            if (isSpecialCharOrChinese(before, after)) {
                if (replaceString.contains(COMMA)) {
                    if(COMMA.equals(before)){
                        pendingString = pendingString.replaceFirst(StrUtil.BACKSLASH + COMMA + representFactor + StrUtil.BACKSLASH + after, StrUtil.BACKSLASH + before + templateStringByRepresentFactor(representFactor, replaceString) + StrUtil.BACKSLASH + after);
                    }else if(isChinese(before)||isChinese(after)){
                        pendingString = pendingString.replaceFirst(ReUtil.escape(before + representFactor  + after), ReUtil.escape(before + templateStringByRepresentFactor(representFactor, replaceString)+ after));
                    }else{
                        Pattern compile = Pattern.compile(ReUtil.escape(before) + representFactor + "(?<param>.*?)\\{\\s*\\}", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
                        Matcher matcher = compile.matcher(pendingString);
                        while(matcher.find()){
                            final String param = matcher.group(PARAM);
                            if(QUOTATION_MARKS.equals(before)){
                                final String collect = Stream.of(replaceString.split(COMMA)).map(s -> templateStringByRepresentFactor(representFactor, s)).map(s ->  s + param + StrUtil.EMPTY_JSON).collect(Collectors.joining(" "));
                                pendingString = pendingString.replaceFirst(StrUtil.BACKSLASH + before + representFactor+param+ "\\{\\s*\\}", StrUtil.BACKSLASH + before+collect);
                            }else{
                                String finalBefore = before;
                                final String collect = Stream.of(replaceString.split(COMMA)).map(s -> templateStringByRepresentFactor(representFactor, s)).map(s -> StrUtil.BACKSLASH + finalBefore + s + param + "\\{\\s*\\}").collect(Collectors.joining(" "));
                                pendingString = pendingString.replaceFirst(StrUtil.BACKSLASH + before + representFactor+param+ "\\{\\s*\\}", collect);
                            }
                        }

                        compile = Pattern.compile("(?<paramPrefix>.*?)@param"+ReUtil.escape(before) + representFactor + ReUtil.escape(after)+"(?<paramSuffix>.*)", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
                        matcher = compile.matcher(pendingString);
                        while(matcher.find()){
                            final String paramSuffix = matcher.group(PARAM_SUFFIX);
                            final String paramPrefix = matcher.group(PARAM_PREFIX);
                            String finalAfter = after;
                            String finalBefore = before;
                            final String collect = Stream.of(replaceString.split(COMMA)).map(s -> templateStringByRepresentFactor(representFactor, s)).map(s ->  paramPrefix+"@param"+ finalBefore + s+ finalAfter + replace(paramSuffix,representFactor,s)).collect(Collectors.joining("\r\n"));
                            pendingString = pendingString.replaceFirst(ReUtil.escape(paramPrefix+"@param"+before + representFactor + after+paramSuffix), ReUtil.escape(collect));
                        }
                    }
                } else {
                    pendingString = pendingString.replaceFirst(ReUtil.escape(before + representFactor + after), ReUtil.escape(before + templateStringByRepresentFactor(representFactor, replaceString) +after));
                }
            }else{
                break;
            }
        }
        return pendingString;
    }

    default String getAfter(String pendingString, String representFactor, int representFactorIndex) {
        String after;
        try {
            after = pendingString.substring(representFactorIndex + representFactor.length(), representFactorIndex + representFactor.length() + 1);
        }catch (Exception e){
            after= StrUtil.EMPTY;
        }
        return after;
    }

    default String getBefore(String pendingString, int representFactorIndex) {
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
    default boolean isSpecialCharOrChinese(String before, String after) {
        return (
                        (SPECIAL_CHAR.contains(before) && SPECIAL_CHAR.contains(after))
                        ||
                        (isChinese(before)&& isChinese(after))
                        ||
                        (isChinese(before)&& SPECIAL_CHAR.contains(after))
                        ||
                        (SPECIAL_CHAR.contains(before) && isChinese(after))
                        ||
                        (isChinese(before)&&StrUtil.EMPTY.equals(after))
                        ||
                        (isChinese(after)&&StrUtil.EMPTY.equals(before))
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
