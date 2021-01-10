package com.fpp.code.core.template;

import com.fpp.code.common.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * foreach 语句解析器
 * @author fpp
 * @version 1.0
 * @date 2020/6/9 16:50
 */
public class ForeachTemplateResolver extends AbstractTemplateLangResolver{

    private static final String LANG_NAME="foreach";

    public ForeachTemplateResolver() {
        super();
        this.resolverName=LANG_NAME;
    }

    public ForeachTemplateResolver(TemplateResolver templateResolver) {
        super(templateResolver);
        this.resolverName=LANG_NAME;
    }

    private static final Pattern templateFunctionBodyPattern= Pattern.compile("(\\s*?)(" + AbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX + "\\s*"+LANG_NAME+"\\s*v-for=[\"|\'](?<title>.*?)[\"|\']" + AbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX + ")(?<body>.*?)(" + AbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX + "\\s*/"+LANG_NAME+"\\s*" + AbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX + ")(\\s*?)", Pattern.DOTALL);
    private static final Pattern templateGrammarPatternPrefix= Pattern.compile("(\\s*"+LANG_NAME+"\\s*v-for=[\"|\'](?<title>.*?)[\"|\'])", Pattern.DOTALL);
    private static final Pattern templateGrammarPatternSuffix= Pattern.compile("(\\s*/"+LANG_NAME+"\\s*)", Pattern.DOTALL);
    private Set<Pattern> excludeVariablePatten=new HashSet<>(Arrays.asList(templateGrammarPatternPrefix, templateGrammarPatternSuffix));
    /**
     * 模板语言解析方法
     *
     * @param srcData         需要解析的模板数据
     * @param replaceKeyValue 模板中的变量数据
     */
    @Override
    public String langResolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException {
        String result="";
        Matcher matcher = templateFunctionBodyPattern.matcher(srcData);
        while (matcher.find()) {
            String forEachBody = matcher.group("body");
            String forEachTitle = matcher.group("title");
            String forEachAll=matcher.group(0);
            String[] titleArray=forEachTitle.split("in");
            if(!forEachTitle.contains("in")||titleArray.length!=2){
                throw new TemplateResolveException(LANG_NAME+" 语句 语法异常");
            }
            String itemName=titleArray[0].trim();
            String itemParentNode=titleArray[1].trim();
            //校验字段是否存在,并返回最终的最后的对象 a.b.c 返回a对象中的b对象中的c对象
            Object temp=Utils.getTargetObject(replaceKeyValue,itemParentNode);
            String foreachResult=getLangBodyResult(temp,forEachBody,itemName);
            result= Utils.isEmpty(result)?srcData.replace(forEachAll,foreachResult):result.replace(forEachAll,foreachResult);
        }
        return Utils.isEmpty(result)?srcData:result;
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
