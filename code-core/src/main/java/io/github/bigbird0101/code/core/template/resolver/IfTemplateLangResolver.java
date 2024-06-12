package io.github.bigbird0101.code.core.template.resolver;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.template.AbstractAbstractTemplateResolver;
import io.github.bigbird0101.code.core.template.AbstractTemplateLangResolver;
import io.github.bigbird0101.code.core.template.ConditionJudgeSupport;
import io.github.bigbird0101.code.core.template.TemplateResolver;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import io.github.bigbird0101.code.util.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author fpp
 * @version 1.0
 * @since 2020/6/11 18:42
 */
public class IfTemplateLangResolver extends AbstractTemplateLangResolver {
    private final ConditionJudgeSupport conditionJudgeSupport=new ConditionJudgeSupport();

    private static final String LANG_NAME="if";
    private final Set<Pattern> excludeVariablePatten=new HashSet<>(Arrays.asList(TEMPLATE_GRAMMAR_PATTERN_PREFIX,TEMPLATE_GRAMMAR_PATTERN_SUFFIX));

    public IfTemplateLangResolver() {
        super();
        this.resolverName=LANG_NAME;
    }

    public IfTemplateLangResolver(TemplateResolver template) {
        super(template);
        this.resolverName=LANG_NAME;
    }

    private static final Pattern TEMPLATE_FUNCTION_BODY_PATTERN = Pattern.compile("(\\s*?)(" + AbstractAbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX_ESCAPE + "\\s*" + LANG_NAME + "\\s*v-if=[\"|\'](?<title>.*?)[\"|\']" + AbstractAbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX_ESCAPE + ")(?<body>.*?)(" + AbstractAbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX_ESCAPE + "\\s*/" + LANG_NAME + "\\s*" + AbstractAbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX_ESCAPE + ")(\\s*?)", Pattern.DOTALL);
    private static final Pattern TEMPLATE_GRAMMAR_PATTERN_PREFIX = Pattern.compile("(\\s*"+LANG_NAME+"\\s*v-if=[\"|\'](?<title>.*?)[\"|\'])", Pattern.DOTALL);
    private static final Pattern TEMPLATE_GRAMMAR_PATTERN_SUFFIX = Pattern.compile("(\\s*/"+LANG_NAME+"\\s*)", Pattern.DOTALL);

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
            String body = matcher.group("body");
            String title = matcher.group("title");
            String all=matcher.group(0);
            try {
                //是否满足if条件
                String temp = Utils.getFirstNewLineNull(body);
                if("true".equalsIgnoreCase(title)||"false".equalsIgnoreCase(title)){
                    String bodyResult = Boolean.parseBoolean(title) ? temp + body.trim() : "";
                    result = Utils.isEmpty(result) ? StrUtil.isNotBlank(result)?result.replace(all, bodyResult):srcData.replace(all, bodyResult) : result.replace(all, bodyResult);
                }else{
                    result=doSpecialExpression(dataModal,title,body,StrUtil.isNotBlank(result)?result:srcData,all);
                }
            }catch(Exception e) {
                result=doSpecialExpression(dataModal,title,body,StrUtil.isNotBlank(result)?result:srcData,all);
            }
        }
        return result;
    }

    /**
     * 处理特殊的表达式 包含变量
     * @param replaceKeyValue
     * @param title
     * @param body
     * @param srcData
     * @param all
     * @return
     * @throws TemplateResolveException
     */
    public String doSpecialExpression(Map<String, Object> replaceKeyValue,String title,String body,String srcData,String all) throws TemplateResolveException {
        String result="";
        List<String> targetKeyList=conditionJudgeSupport.checkFiled(replaceKeyValue,title);
        if(conditionJudgeSupport.meetConditions(title,targetKeyList,replaceKeyValue)) {
            for (String targetKey : targetKeyList) {
                String bodyResult = getLangBodyResult(replaceKeyValue.get(targetKey), body, targetKey);
                result = Utils.isEmpty(result) ? srcData.replace(all, bodyResult) : result.replace(all, bodyResult);
            }
        }else{
            result = Utils.isEmpty(result) ? srcData.replace(all, "") : result.replace(all, "");
        }
        return result;
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
