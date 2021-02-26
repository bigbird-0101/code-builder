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
        Matcher matcher3= Pattern.compile("((?<=\\()("+representFactor+")(?=\\)))").matcher(srcFunctionBody);
        while(matcher3.find()){
            srcFunctionBody=srcFunctionBody.replace("("+representFactor+")","("+ Stream.of(definedValues).map(Utils::getFieldName).collect(Collectors.joining(","))+")");
        }
        return srcFunctionBody;
    }
}
