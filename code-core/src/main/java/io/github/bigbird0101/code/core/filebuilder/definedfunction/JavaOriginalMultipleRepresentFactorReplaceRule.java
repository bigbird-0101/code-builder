package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.ReUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 适配原生java
 * 已适配
 * 对于 java 注释 @param  representFactor 备注representFactor 修饰的
 * 替换成  @param 字段A 备注A \r\n  @param 字段B 备注B
 * @author Administrator
 */
public class JavaOriginalMultipleRepresentFactorReplaceRule extends AbstractMultipleRepresentFactorReplaceRule {

    private static final String PARAM = "@param";

    /**
     * 匹配规则
     * 满足java @param注释 规则
     * @param pendingString
     * @param representFactor
     * @param before
     * @param after
     * @return
     */
    @Override
    public boolean doMatch(String pendingString, String representFactor, String before, String after) {
        Pattern compile = Pattern.compile("(?<paramPrefix>.*?)" + PARAM + ReUtil.escape(before) + representFactor + ReUtil.escape(after)+"(?<paramSuffix>.*)", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
        return compile.matcher(pendingString).find();
    }

    @Override
    public String replace(String pendingString, String representFactor, String replaceString, String before, String after) {
        Pattern compile = Pattern.compile("(?<paramPrefix>.*?)" + PARAM + ReUtil.escape(before) + representFactor + ReUtil.escape(after)+"(?<paramSuffix>.*)", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
        final Matcher matcher = compile.matcher(pendingString);
        while(matcher.find()){
            final String paramSuffix = matcher.group(DefinedFunctionResolverRule.PARAM_SUFFIX);
            final String paramPrefix = matcher.group(DefinedFunctionResolverRule.PARAM_PREFIX);
            final String collect = Stream.of(replaceString.split(DefinedFunctionResolverRule.COMMA))
                    .map(s -> templateStringByRepresentFactor(representFactor, s))
                    .map(s ->  paramPrefix+ PARAM + before + s+ after + getRepresentFactorReplaceRuleResolver().doResolver(paramSuffix, representFactor, s))
                    .collect(Collectors.joining("\r\n"));
            pendingString = pendingString.replaceFirst(ReUtil.escape(matcher.group()), ReUtil.escape(collect));
        }
        return pendingString;
    }
}
