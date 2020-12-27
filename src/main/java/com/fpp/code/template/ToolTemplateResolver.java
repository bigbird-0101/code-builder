package com.fpp.code.template;

import com.fpp.code.common.Utils;
import com.fpp.code.config.ProjectFileConfig;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/12 17:49
 */
public class ToolTemplateResolver extends AbstractTemplateLangResolver{

    private static TableInfo tableInfo;
    private static TemplateResolver templateResolver;

    private static String mendLastStr;
    private static String mendFirstStr;

    public ToolTemplateResolver() {
        super();
        this.resolverName=LANG_NAME;
    }

    /**
     * 工具类方法枚举
     */
    private enum Function{
        /**
         * 首字母大写
         */
        FISER_UPPER("firstUpper"){
            @Override
            String done(String src) {
                return Utils.firstUpperCase(src);
            }
        },
        /**
         * 首字母小写
         */
        FISER_LOWER("firstLower") {
            @Override
            String done(String src) {
                return Utils.firstLowerCase(src);
            }
        },
        /**
         * 当前系统时间
         */
        CURRENT_DATE_TIME("currentDateTime"){
            @Override
            String done(String src) {
                return Utils.getCurrentDateTime();
            }
        },
        /**
         * 当前用户
         */
        AUTHOR("author"){
            @Override
            String done(String src) {
                String result=System.getProperty("user.name");
                if(templateResolver instanceof AbstractTemplateResolver&&null!=((AbstractTemplateResolver) templateResolver).getProjectFileConfig()) {
                    ProjectFileConfig projectFileConfig = ((AbstractTemplateResolver) templateResolver).getProjectFileConfig();
                    String author = projectFileConfig.getProperty("project-author");
                    result=Utils.isEmpty(author)?result:author;
                }
                return result;
            }
        },
        /**
         * 获取表的所有SQL字段
         */
        ALL_SQL_COLUMN("allSqlColumn"){
            @Override
            String done(String src) {
                StringBuilder stringBuilder=new StringBuilder();
                int a = 0;
                int currentCount = 0;
                for (TableInfo.ColumnInfo columnInfo : tableInfo.getColumnList()) {
                    if (a == 0) {
                        stringBuilder.append("\"");
                    }
                    String columnName = columnInfo.getName();
                    if (columnName.indexOf("_") > 0) {
                        columnName = columnName + " as " + Utils.getFirstLowerCaseAndSplitLine(columnName);
                    }
                    if (!columnName.contains("_") && columnName.substring(0, 1).toUpperCase().equals(columnName.substring(0, 1))) {
                        columnName = columnName + " as " + columnInfo.getDomainPropertyName();
                    }
                    if (currentCount == tableInfo.getColumnList().size() - 1) {
                        stringBuilder.append(" ").append(columnName);
                    } else {
                        stringBuilder.append(" ").append(columnName).append(",");
                    }
                    a++;
                    currentCount++;
                    if (a == 5 || currentCount == tableInfo.getColumnList().size()) {
                        if (currentCount == tableInfo.getColumnList().size()) {
                            stringBuilder.append(" \",");
                        } else {
                            stringBuilder.append(" \","+mendFirstStr);
                            a = 0;
                        }
                    }

                }
                return stringBuilder.toString();
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
        abstract String done(String src);
    }

    private static final String LANG_NAME="tool";
    private static final Pattern templateFunctionBodyPattern= Pattern.compile("(\\s*"+AbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX+"\\s*"+LANG_NAME+"\\s*\\.(?<function>.*?)\\(\\s*(?<title>.*?)\\s*\\)\\s*"+AbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX+"\\s*)", Pattern.DOTALL);
    protected static final Pattern templateGrammarPatternSuffix= Pattern.compile("(\\s*"+LANG_NAME+"\\s*\\.(?<function>.*?)\\(\\s*(?<title>.*?)\\s*)", Pattern.DOTALL);
    private Set<Pattern> excludeVariablePatten=new HashSet<>(Arrays.asList(templateGrammarPatternSuffix));

    public ToolTemplateResolver(TemplateResolver templateResolver) {
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
            String title = matcher.group("title");
            String function = matcher.group("function");
            String all=matcher.group(0);
            Matcher matcherTitle=AbstractTemplateResolver.templateVariableKeyPattern.matcher(title);
            StringBuilder titleBuilder=new StringBuilder();
            while(matcherTitle.find()){
                String titleGroup=matcherTitle.group();
                Object objectTarget=Utils.getTargetObject(replaceKeyValue,titleGroup);
                String realTitle;
                if(objectTarget instanceof String){
                    realTitle= (String) objectTarget;
                }else {
                    realTitle = getLangBodyResult(objectTarget, title, titleGroup.split("\\.")[0]);
                }
                titleBuilder.append(realTitle);
            }
            title=titleBuilder.length()==0?title:titleBuilder.toString();
            Function functionTemp=checkFunction(function);
            mendLastStr=Utils.getLastNewLineNull(all);
            mendFirstStr=Utils.getFirstNewLineNull(all);
            String bodyResult=functionTemp.done(title);
            bodyResult=mendFirstStr+bodyResult+mendLastStr;
            result = Utils.isEmpty(result) ? srcData.replace(all, bodyResult) : result.replace(all, bodyResult);
        }
        return Utils.isEmpty(result)?srcData:result;
    }

    private Function checkFunction(String functionName) {
        //校验工具方法是否存在
        Function result=null;
        for(Function function:Function.values()){
            if(function.getValue().equals(functionName)){
                result=function;
            }
        }
        Objects.requireNonNull(result,functionName+"在"+LANG_NAME+"中不存在");
        return result;
    }

}
