package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import io.github.bigbird0101.code.core.domain.DefinedFunctionDomain;
import io.github.bigbird0101.code.core.filebuilder.TimeoutRegexCharSequence;
import io.github.bigbird0101.code.core.domain.TableInfo;
import io.github.bigbird0101.code.core.template.TemplateTraceContext;
import io.github.bigbird0101.code.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 方法参数解析规则
 * @author fpp
 * @version 1.0
 * @since 2020/7/13 9:28
 */
public class FunctionParamDefinedFunctionResolverRule extends AbstractDefinedFunctionResolverRule {

    @Override
    public String doRule(DefinedFunctionDomain definedFunctionDomain) {
        String definedValue=definedFunctionDomain.getDefinedValue();
        String representFactor=definedFunctionDomain.getRepresentFactor();
        String srcFunctionBody=definedFunctionDomain.getTemplateFunction();
        TableInfo tableInfo=definedFunctionDomain.getTableInfo();
        //解析方法体中的参数
        String lowerRepresentFactor= Utils.firstLowerCase(Utils.underlineToHump(representFactor));
        final boolean isInterface = isInterface(TemplateTraceContext.getCurrentTemplate());
        Matcher matcherFunction=isInterface?INTERFACE_FUNCTION.matcher(srcFunctionBody):FUNCTION.matcher(srcFunctionBody);
        if (matcherFunction.find()){
            String paramContent=matcherFunction.group(FUNCTION_PARAM);
            if(Pattern.compile(lowerRepresentFactor, Pattern.CASE_INSENSITIVE).matcher(paramContent).find()) {
                Matcher matcher2=Pattern.compile("(?<paramPrefix>.*?)\\s+"+lowerRepresentFactor+"\\s*",
                        Pattern.CASE_INSENSITIVE).matcher(paramContent);
                try {
                    if(matcher2.find()) {
                        String paramPrefix = matcher2.group("paramPrefix");
                        String[] valueS=paramPrefix.split("\\s");
                        String paramPrefixReal=Stream.of(valueS).limit(valueS.length-1).collect(Collectors.joining());
                        String newParamContent = Stream.of(definedValue.split(","))
                                .map(s->Utils.firstLowerCase(Utils.underlineToHump(s)))
                                .map(s -> Utils.isEmpty(paramPrefixReal) ?TableInfo.getJavaType(s,tableInfo)+" " + s
                                        : paramPrefixReal.replaceAll(lowerRepresentFactor,s) + " " +TableInfo.getJavaType(s,tableInfo) +" "+ s)
                                .collect(Collectors.joining(","));
                        srcFunctionBody = srcFunctionBody.replace(paramContent, newParamContent);
                    }
                }catch (Exception ignored){
                }
            }
        }
        return srcFunctionBody;
    }

    public static Matcher createMatcherWithTimeout(String stringToMatch, String regularExpression, int timeoutMillis) {
        Pattern pattern = Pattern.compile(regularExpression);
        return createMatcherWithTimeout(stringToMatch, pattern, timeoutMillis);
    }

    public static Matcher createMatcherWithTimeout(String stringToMatch, Pattern regularExpressionPattern, int timeoutMillis) {
        CharSequence charSequence = new TimeoutRegexCharSequence(stringToMatch, timeoutMillis, stringToMatch,
                regularExpressionPattern.pattern());
        return regularExpressionPattern.matcher(charSequence);
    }
}
