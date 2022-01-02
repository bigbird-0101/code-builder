package com.fpp.code.core.filebuilder.definedfunction;

import com.fpp.code.common.Utils;
import com.fpp.code.core.domain.DefinedFunctionDomain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 解析方法体中的方法的方法实参 规则
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 9:47
 */
public class FunctionInnerParamDefinedFunctionResolverRule implements DefinedFunctionResolverRule {
    /**
     * 将模板方法根据规则解析成自定义方法
     *
     * @param definedFunctionDomain
     * @return 解析后自定义方法
     */
    @Override
    public String doRule(DefinedFunctionDomain definedFunctionDomain) {
        String definedValue=definedFunctionDomain.getDefinedValue();
        String representFactor=definedFunctionDomain.getRepresentFactor();
        String srcFunctionBody=definedFunctionDomain.getTemplateFunction();
        String[] definedValues=definedValue.split("\\,");
        //匹配不是set方法的
        Matcher matcher3= Pattern.compile("(.*?)(?<!set"+ Utils.firstUpperCase(representFactor)+")\\("+representFactor+"\\)", Pattern.CASE_INSENSITIVE).matcher(srcFunctionBody);
        while(matcher3.find()){
            String group = matcher3.group();
            String group1=matcher3.group(1);
            String result="("+ Stream.of(definedValues).map(s->Utils.firstLowerCase(Utils.underlineToHump(s))).collect(Collectors.joining(","))+")";
            srcFunctionBody=srcFunctionBody.replace(group,group1+result);
        }
        //匹配是set方法的 解决多个字段的set方法生成
        matcher3= Pattern.compile("(.*?)(set)("+ Utils.firstUpperCase(representFactor)+")\\("+representFactor+"\\)", Pattern.CASE_INSENSITIVE).matcher(srcFunctionBody);
        while(matcher3.find()){
            String group = matcher3.group();
            String group1=matcher3.group(1);
            String group2 = matcher3.group(2);
            String result=Stream.of(definedValues).map(s->group1+group2+Utils.firstUpperCase(Utils.underlineToHump(s))+"("+Utils.firstLowerCase(Utils.underlineToHump(s))+")").collect(Collectors.joining(";\r\n"));
            srcFunctionBody=srcFunctionBody.replace(group,result);
        }
        return srcFunctionBody;
    }
}
