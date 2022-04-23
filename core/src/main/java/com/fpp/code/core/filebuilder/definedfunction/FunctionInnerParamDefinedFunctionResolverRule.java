package com.fpp.code.core.filebuilder.definedfunction;

import com.fpp.code.core.domain.DefinedFunctionDomain;
import com.fpp.code.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 解析方法体中的方法的方法实参 规则
 *
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 9:47
 */
public class FunctionInnerParamDefinedFunctionResolverRule implements DefinedFunctionResolverRule {
    private static final Pattern FUNCTION = Pattern.compile("(?<functionPrefix>.*?)\\s*\\(\\s*(?<functionParam>.*?)\\s*\\)\\s*\\{\\s*(?<functionBody>.*{2,5})\\s*\\}", Pattern.DOTALL);
    public static final String FUNCTION_BODY = "functionBody";

    /**
     * 将模板方法根据规则解析成自定义方法
     *
     * @param definedFunctionDomain
     * @return 解析后自定义方法
     */
    @Override
    public String doRule(DefinedFunctionDomain definedFunctionDomain) {
        String definedValue = definedFunctionDomain.getDefinedValue();
        String representFactor = definedFunctionDomain.getRepresentFactor();
        String srcFunctionBody = definedFunctionDomain.getTemplateFunction();
        String[] definedValues = definedValue.split("\\,");
        //匹配不是set方法的
        Matcher matcher = Pattern.compile("(.*?)(?<!set" + Utils.firstUpperCase(representFactor) + ")\\(" + representFactor + "\\)", Pattern.CASE_INSENSITIVE).matcher(srcFunctionBody);
        while (matcher.find()) {
            String group = matcher.group();
            String group1 = matcher.group(1);
            String result = "(" + Stream.of(definedValues).map(s -> Utils.firstLowerCase(Utils.underlineToHump(s))).collect(Collectors.joining(",")) + ")";
            srcFunctionBody = srcFunctionBody.replace(group, group1 + result);
        }
        //匹配是set方法的 解决多个字段的set方法生成
        matcher = Pattern.compile("(.*?)(set)(" + Utils.firstUpperCase(representFactor) + ")\\(" + representFactor + "\\)", Pattern.CASE_INSENSITIVE).matcher(srcFunctionBody);
        while (matcher.find()) {
            String group = matcher.group();
            String group1 = matcher.group(1);
            String group2 = matcher.group(2);
            String result = Stream.of(definedValues).map(s -> group1 + group2 + Utils.firstUpperCase(Utils.underlineToHump(s)) + "(" + Utils.firstLowerCase(Utils.underlineToHump(s)) + ")").collect(Collectors.joining(";\r\n"));
            srcFunctionBody = srcFunctionBody.replace(group, result);
        }

        matcher = FUNCTION.matcher(srcFunctionBody);
        if (matcher.find()) {
            String functionBody = matcher.group(FUNCTION_BODY);
            String oldFunctionBody = functionBody;
            functionBody = replace(functionBody, representFactor, definedValue);
            srcFunctionBody = srcFunctionBody.replace(oldFunctionBody, functionBody);
        }
        return srcFunctionBody;
    }



}
