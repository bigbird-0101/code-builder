package com.fpp.code.core.template;

import com.fpp.code.util.Utils;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.context.TemplateContext;
import com.fpp.code.core.context.aware.TemplateContextAware;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/12 17:49
 */
public class DependTemplateResolver extends AbstractTemplateLangResolver implements TemplateContextAware {
    private static org.apache.logging.log4j.Logger logger= LogManager.getLogger(DependTemplateResolver.class);

    private static TableInfo tableInfo;
    private static TemplateResolver templateResolver;
    private static TemplateContext templateContext;
    private static String mendLastStr;
    private static String mendFirstStr;

    public DependTemplateResolver() {
        super();
        this.resolverName=LANG_NAME;
    }

    @Override
    public void setTemplateContext(TemplateContext templateContext) {
        this.templateContext=templateContext;
    }

    /**
     * 工具类方法枚举
     */
    private enum Function{
        /**
         * 所依赖的模板类名
         */
        CLASS_NAME("className"){
            @Override
            String done(String index) throws TemplateResolveException {
                Template currentTemplate = TemplateTraceContext.getCurrentTemplate();
                if(currentTemplate instanceof HaveDependTemplateHandleFunctionTemplate){
                    HaveDependTemplateHandleFunctionTemplate template=(HaveDependTemplateHandleFunctionTemplate)currentTemplate;
                    int i;
                    try {
                        i = Integer.parseInt(index);
                    }catch (Exception e){
                        throw new TemplateResolveException(String.format("Integer.parseInt %s error to get template",index));
                    }
                    String templateName = getSetValue(template.getDependTemplates(), i);
                    Template templateDepend;
                    try {
                        templateDepend = templateContext.getTemplate(templateName);
                    } catch (CodeConfigException | IOException e) {
                        throw new TemplateResolveException(e);
                    }
                    TableInfo tableInfo = (TableInfo) template.getTemplateVariables().get("tableInfo");
                    return templateDepend.getSrcPackage().replaceAll("\\/", ".") +"."+tableInfo.getDomainName();
                }else{
                    throw new TemplateResolveException(String.format("current template %s is not HaveDependTemplateHandleFunctionTemplate",currentTemplate.getTemplateName()));
                }
            }
        },
        SRC_PACKAGE("srcPackage"){
            @Override
            String done(String index) throws TemplateResolveException {
                Template currentTemplate = TemplateTraceContext.getCurrentTemplate();
                if(currentTemplate instanceof HaveDependTemplateHandleFunctionTemplate){
                    HaveDependTemplateHandleFunctionTemplate template=(HaveDependTemplateHandleFunctionTemplate)currentTemplate;
                    int i;
                    try {
                        i = Integer.parseInt(index);
                    }catch (Exception e){
                        throw new TemplateResolveException(String.format("Integer.parseInt %s error to get template",index));
                    }
                    String templateName = getSetValue(template.getDependTemplates(), i);
                    Template templateDepend;
                    try {
                        templateDepend = templateContext.getTemplate(templateName);
                    } catch (CodeConfigException | IOException e) {
                        throw new TemplateResolveException(e);
                    }
                    return templateDepend.getSrcPackage().replaceAll("\\/", ".");
                }else{
                    throw new TemplateResolveException(String.format("current template %s is not HaveDependTemplateHandleFunctionTemplate",currentTemplate.getTemplateName()));
                }
            }
        }
        ;

        private String value;
        public String getValue() {
            return value;
        }
        Function(String value) {
            this.value = value;
        }
        abstract String done(String index) throws TemplateResolveException;
    }

    private static final String LANG_NAME="depend";
    private static final Pattern templateFunctionBodyPattern= Pattern.compile("(\\s*"+AbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX+"\\s*"+LANG_NAME+"\\s*\\[\\s*(?<index>.*?)\\s*\\]\\s*\\.(?<function>.*?)\\(\\s*(?<title>.*?)\\s*\\)\\s*"+AbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX+"\\s*)", Pattern.DOTALL);
    protected static final Pattern templateGrammarPatternSuffix= Pattern.compile("(\\s*"+LANG_NAME+"\\s*[\\s*.*?\\s*]\\s*\\.(?<function>.*?)\\(\\s*(?<title>.*?)\\s*)", Pattern.DOTALL);
    private Set<Pattern> excludeVariablePatten=new HashSet<>(Arrays.asList(templateGrammarPatternSuffix));

    public DependTemplateResolver(TemplateResolver templateResolver) {
        super(templateResolver);
        this.resolverName=LANG_NAME;
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

    /**
     * 模板语言解析方法
     *
     * @param srcData         需要解析的模板数据
     * @param replaceKeyValue 模板中的变量数据
     */
    @Override
    public String langResolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException {
        tableInfo= (TableInfo) replaceKeyValue.get("tableInfo");
        templateResolver=this.getTemplateResolver();
        Matcher matcher=templateFunctionBodyPattern.matcher(srcData);
        String result="";
        while(matcher.find()){
            String function = matcher.group("function");
            String index = matcher.group("index");
            String all=matcher.group(0);
            Function functionTemp=checkFunction(function);
            mendLastStr=Utils.getLastNewLineNull(all);
            mendFirstStr=Utils.getFirstNewLineNull(all);
            String bodyResult=functionTemp.done(index);
            bodyResult=mendFirstStr+bodyResult+mendLastStr;
            result = Utils.isEmpty(result) ? srcData.replace(all, bodyResult) : result.replace(all, bodyResult);
        }
        return Utils.isEmpty(result)?srcData:result;
    }

    private Function checkFunction(String functionName) {
        //校验工具方法是否存在
        Function result=null;
        for(Function function: Function.values()){
            if(function.getValue().equals(functionName)){
                result=function;
            }
        }
        Objects.requireNonNull(result,functionName+"在"+LANG_NAME+"中不存在");
        return result;
    }

    private static String getSetValue(Set<? extends String> set, int index) {
        int result = 0;
        for (String entry:set) {
            if(result==index){
                return entry;
            }
            result++;
        }
        return "";
    }
}
