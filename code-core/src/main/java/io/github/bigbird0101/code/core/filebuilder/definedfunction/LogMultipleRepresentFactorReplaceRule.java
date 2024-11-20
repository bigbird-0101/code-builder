package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.text.StrPool.BACKSLASH;

/**
 * 适配日志模块
 * 带有 representFactor {} 替换成 多个字段 的  字段A {},字段B {}
 * @author Administrator
 */
public class LogMultipleRepresentFactorReplaceRule extends AbstractMultipleRepresentFactorReplaceRule {
    private static final String QUOTATION_MARKS ="\"";
    private static final String PARAM ="param";

    @Override
    protected boolean doMatch(String pendingString, String representFactor, String before, String after) {
        Pattern compile = Pattern.compile(ReUtil.escape(before) + representFactor + "(?<param>.*?)\\{\\s*}");
        return false;
    }

    @Override
    public String replace(Map<String, Object> dataModel, String pendingString, String representFactor, String replaceString, String before, String after) {
        Pattern compile = Pattern.compile(ReUtil.escape(before) + representFactor + "(?<param>.*?)\\{\\s*}");
        Matcher matcher = compile.matcher(pendingString);
        while(matcher.find()){
            final String param = matcher.group(PARAM);
            if(QUOTATION_MARKS.equals(before)){
                final String collect = Stream.of(replaceString.split(COMMA))
                        .map(s -> templateStringByRepresentFactor(representFactor, s))
                        .map(s ->  s + param + StrUtil.EMPTY_JSON)
                        .collect(Collectors.joining(" "));
                pendingString = pendingString.replaceFirst(BACKSLASH + before + representFactor + param + "\\{\\s*\\}",
                        BACKSLASH + before + collect);
            }else{
                final String collect = Stream.of(replaceString.split(COMMA))
                        .map(s -> templateStringByRepresentFactor(representFactor, s))
                        .map(s -> BACKSLASH + before + s + param + "\\{\\s*\\}")
                        .collect(Collectors.joining(" "));
                pendingString = pendingString.replaceFirst(ReUtil.escape(matcher.group()),
                        collect);
            }
        }
        return pendingString;
    }
}
