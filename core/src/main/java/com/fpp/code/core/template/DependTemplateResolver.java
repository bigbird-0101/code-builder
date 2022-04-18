package com.fpp.code.core.template;

import com.fpp.code.core.context.TemplateContext;
import com.fpp.code.core.context.aware.TemplateContextAware;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/12 17:49
 */
public class DependTemplateResolver extends AbstractTemplateLangResolver implements TemplateContextAware {
    private static Logger logger= LogManager.getLogger(DependTemplateResolver.class);

    private TemplateContext templateContext;

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
         * 所依赖的模板全类名
         */
        SIMPLE_CLASS_NAME("simpleClassName"){
            @Override
            String done(String index) throws TemplateResolveException {
                Template currentTemplate = TemplateTraceContext.getCurrentTemplate();
                if(currentTemplate instanceof HaveDependTemplate){
                    Template templateDepend = getDependTemplate(index, (HaveDependTemplate) currentTemplate);
                    TableInfo tableInfo = (TableInfo) currentTemplate.getTemplateVariables().get("tableInfo");
                    return templateDepend.getTemplateFilePrefixNameStrategy().prefixStrategy(templateDepend,tableInfo.getTableName());
                }else{
                    throw new TemplateResolveException(String.format("current template %s is not HaveDependTemplate,but it use  %s[%s].%s",currentTemplate.getTemplateName(),LANG_NAME,index,"className"));
                }
            }
        },
        /**
         * 所依赖的模板全类名
         */
        CLASS_NAME("className"){
            @Override
            String done(String index) throws TemplateResolveException {
                Template currentTemplate = TemplateTraceContext.getCurrentTemplate();
                if(currentTemplate instanceof HaveDependTemplate){
                    Template templateDepend = getDependTemplate(index, (HaveDependTemplate) currentTemplate);
                    TableInfo tableInfo = (TableInfo) currentTemplate.getTemplateVariables().get("tableInfo");
                    return Utils.pathToPackage(templateDepend.getSrcPackage())+"."+
                            templateDepend.getTemplateFilePrefixNameStrategy().prefixStrategy(templateDepend,tableInfo.getTableName());
                }else{
                    throw new TemplateResolveException(String.format("current template %s is not HaveDependTemplate,but it use  %s[%s].%s",currentTemplate.getTemplateName(),LANG_NAME,index,"className"));
                }
            }
        },
        PACKAGE_NAME("packageName"){
            @Override
            String done(String index) throws TemplateResolveException {
                Template currentTemplate = TemplateTraceContext.getCurrentTemplate();
                if(currentTemplate instanceof HaveDependTemplate){
                    Template templateDepend = getDependTemplate(index, (HaveDependTemplate) currentTemplate);
                    return Utils.pathToPackage(templateDepend.getSrcPackage());
                }else{
                    throw new TemplateResolveException(String.format("current template %s is not HaveDependTemplate but it  %s[%s].%s",currentTemplate.getTemplateName(),LANG_NAME,index,"srcPackage"));
                }
            }
        }
        ;

        private TemplateContext templateContext;
        private String value;
        public String getValue() {
            return value;
        }
        public TemplateContext getTemplateContext() {
            return templateContext;
        }
        public void setTemplateContext(TemplateContext templateContext) {
            this.templateContext = templateContext;
        }
        Function(String value) {
            this.value = value;
        }
        abstract String done(String index) throws TemplateResolveException;


        Template getDependTemplate(String index, HaveDependTemplate currentTemplate) throws TemplateResolveException {
            int i;
            try {
                i = Integer.parseInt(index);
            }catch (Exception e){
                throw new TemplateResolveException(String.format("get index %s depend template error to get template",index));
            }
            String templateName = getSetValue(currentTemplate.getDependTemplates(), i);
            Template templateDepend;
            try {
                templateDepend = getTemplateContext().getTemplate(templateName);
            } catch (CodeConfigException e) {
                throw new TemplateResolveException(e);
            }
            return templateDepend;
        }
    }

    private static final String LANG_NAME="depend";
    private static final Pattern TEMPLATE_FUNCTION_BODY_PATTERN = Pattern.compile("(\\s*"+AbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX+"\\s*"+LANG_NAME+"\\s*\\[\\s*(?<index>.*?)\\s*\\]\\s*\\.(?<function>.*?)\\(?\\s*\\s*\\)??\\s*"+AbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX+"\\s*)", Pattern.DOTALL);
    protected static final Pattern TEMPLATE_GRAMMAR_PATTERN_SUFFIX = Pattern.compile("(\\s*"+LANG_NAME+"\\s*[\\s*.*?\\s*]\\s*\\.(?<function>.*?)\\(?\\s*\\)?\\s*)", Pattern.DOTALL);
    private Set<Pattern> excludeVariablePatten=new HashSet<>(Collections.singletonList(TEMPLATE_GRAMMAR_PATTERN_SUFFIX));

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
        Matcher matcher= TEMPLATE_FUNCTION_BODY_PATTERN.matcher(srcData);
        String result="";
        while(matcher.find()){
            String function = matcher.group("function");
            String index = matcher.group("index");
            String all=matcher.group(0);
            Function functionTemp=checkFunction(function);
            functionTemp.setTemplateContext(templateContext);
            String mendLastStr = Utils.getLastNewLineNull(all);
            String mendFirstStr = Utils.getFirstNewLineNull(all);
            String bodyResult=functionTemp.done(index);
            bodyResult= mendFirstStr +bodyResult+ mendLastStr;
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
