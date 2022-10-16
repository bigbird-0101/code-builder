package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import io.github.bigbird0101.code.core.domain.DefinedFunctionDomain;
import io.github.bigbird0101.code.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 方法名 解析规则
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 9:53
 */
public class FunctionNameDefinedFunctionResolverRule implements DefinedFunctionResolverRule {
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
        String tempLessFunctionName=Stream.of(definedValues).map(Utils::underlineToHump).map(Utils::firstUpperCase).collect(Collectors.joining("And"));
        //解析普通方法
        Pattern rule=Pattern.compile("(.*)\\s+((?<functionNamePrefix>.*)"+Utils.firstUpperCase(representFactor)+"(?<functionNameSuffix>.*?)\\()(.*)(?=\\)\\s*\\{)",Pattern.CASE_INSENSITIVE);
        String tempSrc=srcFunctionBody;
        srcFunctionBody=doRule(srcFunctionBody,rule,tempLessFunctionName,representFactor);
        //解析接口方法
        if(srcFunctionBody.equals(tempSrc)) {
            Pattern ruleInterface = Pattern.compile("(.*)\\s+((?<functionNamePrefix>.*?)" + Utils.firstUpperCase(representFactor) + "(?<functionNameSuffix>.*?)\\()(.*?)(?=\\)\\s*;)", Pattern.CASE_INSENSITIVE);
            srcFunctionBody = doRule(srcFunctionBody, ruleInterface, tempLessFunctionName, representFactor);
        }
        //解析xml当中的方法
        if(srcFunctionBody.equals(tempSrc)) {
            Pattern ruleXml=Pattern.compile("id\\s*=\\s*\"(?<functionNamePrefix>.*?)"+Utils.firstUpperCase(representFactor)+"(?<functionNameSuffix>.*?)\\s*\"{1}?",Pattern.CASE_INSENSITIVE);
            srcFunctionBody = doRule(srcFunctionBody, ruleXml, tempLessFunctionName, representFactor);
        }
        return srcFunctionBody;
    }

    public String doRule(String srcFunctionBody,Pattern rule,String tempLessFunctionName,String representFactor){
        Matcher matcher=rule.matcher(srcFunctionBody);
        if(matcher.find()){
            String prefix=matcher.group("functionNamePrefix");
            String suffix=matcher.group("functionNameSuffix");
            String[] prefixs=prefix.split("\\s");
            String oldFunctionNameCompletion=getFunctionNameCompletion(prefixs,Utils.firstUpperCase(representFactor),suffix);
            try {
                Matcher matcherTemp = Pattern.compile(oldFunctionNameCompletion, Pattern.CASE_INSENSITIVE).matcher(srcFunctionBody);
                return matcherTemp.replaceAll(getFunctionNameCompletion(prefixs,tempLessFunctionName,suffix));
            }catch (Exception e){
                return srcFunctionBody;
            }
        }
        return srcFunctionBody;
    }

    private String getFunctionNameCompletion(String[] perfixs,String realStr,String suffix){
        return perfixs.length>0?(perfixs[perfixs.length-1]+realStr+suffix):realStr+suffix;
    }
}
