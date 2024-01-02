package io.github.bigbird0101.code.core.template.resolver;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.template.AbstractTemplateLangResolver;
import io.github.bigbird0101.code.core.template.AbstractTemplateResolver;
import io.github.bigbird0101.code.core.template.TemplateResolver;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import io.github.bigbird0101.code.util.Utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * foreach 语句解析器
 * @author fpp
 * @version 1.0
 */
public class ForeachTemplateLangResolver extends AbstractTemplateLangResolver {

    private static final String LANG_NAME="foreach";

    private static final List<String> SPECIAL_CHARACTER_LIST = Collections.singletonList(",");
    public ForeachTemplateLangResolver() {
        super();
        this.resolverName=LANG_NAME;
    }

    public ForeachTemplateLangResolver(TemplateResolver templateResolver) {
        super(templateResolver);
        this.resolverName=LANG_NAME;
    }

    private static final Pattern TEMPLATE_FUNCTION_BODY_PATTERN = Pattern.compile("(\\s*?)(" + AbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX_ESCAPE + "\\s*"+LANG_NAME+"\\s*v-for=[\"|\'](?<title>.*?)[\"|\'](\\s*?)(trim=[\"|\'](?<trimValue>.*?)[\"|\'])?(\\s*?)" + AbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX_ESCAPE + ")(?<body>.*?)(" + AbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX_ESCAPE + "\\s*/"+LANG_NAME+"\\s*" + AbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX_ESCAPE + ")(\\s*?)", Pattern.DOTALL);
    private static final Pattern TEMPLATE_GRAMMAR_PATTERN_PREFIX = Pattern.compile("(\\s*"+LANG_NAME+"\\s*v-for=[\"|\'](?<title>.*?)[\"|\'])(\\s*?)(trim=[\"|\'](?<trimValue>.*?)[\"|\'])?(\\s*?)", Pattern.DOTALL);
    private static final Pattern TEMPLATE_GRAMMAR_PATTERN_SUFFIX = Pattern.compile("(\\s*/"+LANG_NAME+"\\s*)", Pattern.DOTALL);
    private final Set<Pattern> excludeVariablePatten=new HashSet<>(Arrays.asList(TEMPLATE_GRAMMAR_PATTERN_PREFIX, TEMPLATE_GRAMMAR_PATTERN_SUFFIX));

    @Override
    public boolean matchLangResolver(String srcData) {
        return TEMPLATE_FUNCTION_BODY_PATTERN.matcher(srcData).find();
    }

    /**
     * 模板语言解析方法
     *
     * @param srcData         需要解析的模板数据
     * @param dataModal 模板中的变量数据
     */
    @Override
    public String langResolver(String srcData, Map<String, Object> dataModal) throws TemplateResolveException {
        String result="";
        Matcher matcher = TEMPLATE_FUNCTION_BODY_PATTERN.matcher(srcData);
        while (matcher.find()) {
            String forEachBody = matcher.group("body");
            String forEachTitle = matcher.group("title");
            String trimValue = matcher.group("trimValue");
            String forEachAll=matcher.group(0);
            List<String> titleArray = Stream.of(forEachTitle.split(" in "))
                    .filter(StrUtil::isNotBlank).collect(toList());
            if(!forEachTitle.contains("in")||titleArray.size()!=2){
                throw new TemplateResolveException(LANG_NAME+" 语句 语法异常");
            }
            String itemName= titleArray.get(0).trim();
            String itemParentNode=titleArray.get(1).trim();
            //校验字段是否存在,并返回最终的最后的对象 a.b.c 返回a对象中的b对象中的c对象
            Object temp= Utils.getTargetObject(dataModal,itemParentNode);
            String foreachResult=getLangBodyResult(temp,forEachBody,itemName);
            //去除最后一个字符为逗号的字符串
            if("true".equals(trimValue)){
                foreachResult=doRemoveTrimCharacter(foreachResult);
            }
            result= Utils.isEmpty(result)?srcData.replace(forEachAll,foreachResult):result.replace(forEachAll,foreachResult);
        }
        return Utils.isEmpty(result)?srcData:result;
    }

    /**
     * 去除字符串首尾两边的特殊符号
     * @param foreachResult foreachResult
     * @return
     */
    private String doRemoveTrimCharacter(String foreachResult) {
        String mendLastStr=Utils.getLastNewLineNull(foreachResult);
        String mendFirstStr=Utils.getFirstNewLineNull(foreachResult);
        String result = foreachResult.trim();
        for(String special:SPECIAL_CHARACTER_LIST) {
            int lengthTrim = result.length();
            if (special.equals(result.substring(lengthTrim - 1))) {
                result = result.substring(0, lengthTrim - 1);
            }
            int lengthTrim2 = result.length();
            if (special.equals(result.substring(0, 1))) {
                result = result.substring(1, lengthTrim2);
            }
        }
        return mendFirstStr+result+mendLastStr;
    }


    /**
     * 获取 模板排除某些正则key 这些正则key是模板中语言的 类型 的set
     *
     * @return
     */
    @Override
    public Set<Pattern> getExcludeVariablePatten() {
        return excludeVariablePatten;
    }

}
