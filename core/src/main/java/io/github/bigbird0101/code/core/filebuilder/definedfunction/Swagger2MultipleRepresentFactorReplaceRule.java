package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.ReUtil;
import cn.hutool.log.StaticLog;
import io.github.bigbird0101.code.core.domain.TableInfo;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.core.template.TemplateTraceContext;
import io.github.bigbird0101.code.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.regex.Pattern.*;

/**
 * 适配 swagger2
 * 对于 java 注释
 * @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "Integer") 修饰的
 * 替换成 @ApiImplicitParams({
 *         @ApiImplicitParam(paramType = "query", name = "name", value = "name", required = true, dataType = "Integer"),
 *         @ApiImplicitParam(paramType = "query", name = "age", value = "age", required = true, dataType = "Integer")
 *     })
 * @author Administrator
 */
public class Swagger2MultipleRepresentFactorReplaceRule extends AbstractMultipleRepresentFactorReplaceRule{
    private static final String API_IMPLICIT_PARAM = "@ApiImplicitParam";
    private static final String PARAM_PREFIX_2 = "paramPrefix2";
    /**
     * Swagger2 类型获取
     */
    private static final Pattern GET_TYPE = compile("\\s*dataType\\s*=\\s*\"\\s*(?<param>.*?)\\s*\"\\s*\\s*", DOTALL| CASE_INSENSITIVE);

    @Override
    protected boolean doMatch(String pendingString, String representFactor, String before, String after) {
        Pattern compile = compile("(?<paramPrefix>.*?)" + API_IMPLICIT_PARAM+"\\s*\\(\\s*(?<paramPrefix2>.*)" + ReUtil.escape(before) + representFactor + ReUtil.escape(after)+"(?<paramSuffix>.*)\\s*\\)\\s*", DOTALL| CASE_INSENSITIVE);
        return compile.matcher(pendingString).find();
    }

    @Override
    public String replace(String pendingString, String representFactor, String replaceString, String before, String after) {
        Pattern compile = compile("(?<paramPrefix>.*?)" + API_IMPLICIT_PARAM +"\\s*\\(\\s*(?<paramPrefix2>.*)"+ ReUtil.escape(before) + representFactor + ReUtil.escape(after)+"(?<paramSuffix>.*)\\s*\\)\\s*", DOTALL| CASE_INSENSITIVE);
        final Matcher matcher = compile.matcher(pendingString);
        while(matcher.find()){
            final String paramSuffix = matcher.group(DefinedFunctionResolverRule.PARAM_SUFFIX);
            final String paramSuffix2 = matcher.group(PARAM_PREFIX_2);
            final String paramPrefix = matcher.group(DefinedFunctionResolverRule.PARAM_PREFIX);
            String prefixNull = Utils.getFirstNewLineNull(paramPrefix);
            String collect = Stream.of(replaceString.split(COMMA))
                    .map(s -> templateStringByRepresentFactor(representFactor, s))
                    .map(s ->  paramPrefix+ API_IMPLICIT_PARAM +"("+getRepresentFactorReplaceRuleResolver()
                            .doResolver(paramSuffix2, representFactor, s)+ before + s+ after +
                            getConvertTypeSuffix(paramSuffix, representFactor, s)+")")
                    .collect(Collectors.joining(",\r\n"));
            collect = prefixNull + "@ApiImplicitParams({\r\n" + collect + "\r\n" + prefixNull + "})";
            pendingString = pendingString.replaceFirst(ReUtil.escape(matcher.group()),ReUtil.escape(collect));
        }
        return pendingString;
    }

    /**
     * 获取到类型转换之后的 后缀
     * @param pendingString
     * @param representFactor
     * @param replaceString
     * @return
     */
    public String getConvertTypeSuffix(String pendingString, String representFactor, String replaceString){
        final Template currentTemplate = TemplateTraceContext.getCurrentTemplate();
        final String targetJavaType = TableInfo.getJavaType(replaceString, currentTemplate);
        String s = getRepresentFactorReplaceRuleResolver().doResolver(pendingString, representFactor, replaceString);
        final Matcher matcher = GET_TYPE.matcher(s);
        if(matcher.find()) {
            try {
                representFactor = matcher.group(0);
                StaticLog.info("getConvertTypeSuffix  src {},target dataType = \"{}\"", representFactor, targetJavaType);
                return getRepresentFactorReplaceRuleResolver().doResolver(s, representFactor,
                        "dataType = \"" + targetJavaType + "\"");
            }catch (Exception e){
                return s;
            }
        }
        return s;
    }
}
