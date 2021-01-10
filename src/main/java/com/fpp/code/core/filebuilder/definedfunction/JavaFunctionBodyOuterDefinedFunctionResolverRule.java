package com.fpp.code.core.filebuilder.definedfunction;

import com.fpp.code.common.Utils;
import com.fpp.code.core.domain.DefinedFunctionDomain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * java 方法体之外解析规则
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 9:40
 */
public class JavaFunctionBodyOuterDefinedFunctionResolverRule implements DefinedFunctionResolverRule {

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
        String[] definedValues=definedValue.split(",");
        Matcher matcher2= Pattern.compile("(?<=\r\n)(?<prefix>.*?)"+representFactor+"(?<suffix>.*?)(?=\r\n)").matcher(srcFunctionBody);
        while(matcher2.find()){
            String prefix=matcher2.group("prefix");
            String suffix=matcher2.group("suffix");
            if(!(prefix+representFactor+suffix).trim().equals("@Override")) {
                String srcCompleteContentLine = prefix + representFactor + suffix;
                if (prefix.contains("@param")) {
                    String newCompleteContentParam = Stream.of(definedValues).map(Utils::getFieldName).map(s -> Utils.replaceIngoreCase(prefix, representFactor, s) + s + Utils.replaceIngoreCase(suffix, representFactor, s)).collect(Collectors.joining("\r\n"));
                    srcFunctionBody = srcFunctionBody.replace(srcCompleteContentLine, newCompleteContentParam);
                }else if (prefix.contains("@ApiImplicitParam")) {
                    String newCompleteContentParam;
                    if (definedValues.length > 1) {
                        String prefixNull = Utils.getFirstNewLineNull(prefix);
                        newCompleteContentParam = Stream.of(definedValues).map(Utils::getFieldName).map(s -> prefixNull + Utils.replaceIngoreCase(prefix, representFactor, s) + s + Utils.replaceIngoreCase(suffix, representFactor, s)).collect(Collectors.joining(",\r\n"));
                        newCompleteContentParam = prefixNull + "@ApiImplicitParams({\r\n" + newCompleteContentParam + "\r\n" + prefixNull + "})";
                        srcFunctionBody = srcFunctionBody.replace(srcCompleteContentLine, newCompleteContentParam);
                    }
                }else if(prefix.trim().startsWith("*")){
                    String newCompleteContentParam = srcCompleteContentLine.replaceAll(representFactor,Stream.of(definedValues).map(Utils::getFieldName).collect(Collectors.joining(",")));
                    srcFunctionBody = srcFunctionBody.replace(srcCompleteContentLine, newCompleteContentParam);
                }else if(prefix.contains("@ApiOperation")){
                    String newCompleteContentParam = prefix+Stream.of(definedValues).map(Utils::getFieldName).collect(Collectors.joining(","))+ suffix;
                    srcFunctionBody = srcFunctionBody.replace(srcCompleteContentLine, newCompleteContentParam);
                }
            }
        }
        return srcFunctionBody;
    }
}
