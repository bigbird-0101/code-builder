package io.github.bigbird0101.code.core.template.resolver;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.context.TemplateContext;
import io.github.bigbird0101.code.core.context.aware.TemplateContextAware;
import io.github.bigbird0101.code.core.domain.TableInfo;
import io.github.bigbird0101.code.core.template.AbstractTemplateLangResolver;
import io.github.bigbird0101.code.core.template.AbstractTemplateResolver;
import io.github.bigbird0101.code.core.template.TemplateResolver;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import io.github.bigbird0101.code.util.Utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author fpp
 * @version 1.0
 */
public class ToolTemplateLangResolver extends AbstractTemplateLangResolver implements TemplateContextAware {

    private static TableInfo tableInfo;
    private static String mendFirstStr;

    private static TemplateContext templateContext;

    public ToolTemplateLangResolver() {
        super();
        this.resolverName=LANG_NAME;
    }

    @Override
    public void setTemplateContext(TemplateContext templateContext) {
        ToolTemplateLangResolver.templateContext =templateContext;
    }

    /**
     * 工具类方法枚举
     */
    private enum Function{
        /**
         * 替换字符串
         * 用新的字符串替换allString中老的字符串
         * tool.rep(allStr,oldStr,newStr)
         */
        REP("rep"){
            @Override
            String done(String src) {
                final LinkedList<String> collect = Stream.of(src.split(","))
                        .collect(Collectors.toCollection(LinkedList::new));
                final int size = collect.size();
                int i = 3;
                if(size != i){
                    throw new TemplateResolveException("tool rep method [{}],src split , size, must = 3 ",src);
                }
                String allString = collect.get(0);
                String oldString = collect.get(1);
                String newString = collect.get(2);
                return allString.replace(oldString,newString);
            }
        },
        /**
         * 只允许
         * tool.sub(1,factory)  截取index 从1开始到末尾的字符串 bc
         * tool.sub(factory,2)  截取index 从开始到末尾倒数2的字符串 a
         * tool.sub(factory,1,2) 截取startIndex 从开始1到2的字符串 这三种语法  b
         * Params:
         * str – String
         * fromIndexInclude – 开始的index（包括）
         * toIndexExclude – 结束的index（不包括）
         */
        SUB("sub"){
            @Override
            String done(String src) {
                final LinkedList<String> collect = Stream.of(src.split(",")).collect(Collectors.toCollection(LinkedList::new));
                final int size = collect.size();
                //只允许 tool.sub(1,factory)  tool.sub(factory,2)  tool.sub(factory,1,2) 这三种语法
                final int i1 = 3;
                if(size <=1||collect.size()> i1){
                    throw new TemplateResolveException("tool sub method not support [{}],src split , is less than 1 or more than 3 ",src);
                }
                final int i = 2;
                final String s = collect.get(0);
                final String s1 = collect.get(1);
                if(i ==collect.size()){
                    if(!NumberUtil.isInteger(s)&&!NumberUtil.isInteger(s1)){
                        throw new TemplateResolveException("tool sub method not support [{}] src all is not number,should example tool.sub(1,factory)  tool.sub(factory,2) ",src);
                    }
                    if(NumberUtil.isInteger(s)){
                        return StrUtil.sub(s1,Integer.parseInt(s),s1.length());
                    }else{
                        return StrUtil.sub(s,0,s.length()-Integer.parseInt(s1));
                    }
                }else{
                    final String s2 = collect.get(2);
                    if(!NumberUtil.isInteger(s1)&&!NumberUtil.isInteger(s2)){
                        throw new TemplateResolveException("tool sub method not support [{}] src suffix all must is number,example tool.sub(factory,1,2) ",src);
                    }
                    return StrUtil.sub(s,Integer.parseInt(s1),Integer.parseInt(s2));
                }
            }
        },
        /**
         * 首字母大写
         */
        FIRST_UPPER("firstUpper"){
            @Override
            String done(String src) {
                return Utils.firstUpperCase(src);
            }
        },
        /**
         * 大写
         */
        UPPER("upper"){
            @Override
            String done(String src) {
                return src.toUpperCase();
            }
        },
        /**
         * 首字母小写
         */
        FIRST_LOWER("firstLower") {
            @Override
            String done(String src) {
                return Utils.firstLowerCase(src);
            }
        },
        /**
         * 小写
         */
        LOWER("lower") {
            @Override
            String done(String src) {
                return src.toLowerCase();
            }
        },
        /**
         * 当前系统时间
         */
        CURRENT_DATE_TIME("currentDateTime"){
            @Override
            String done(String src) {
                return DateUtil.now();
            }
        },
        /**
         * 当前用户
         */
        AUTHOR("author"){
            @Override
            String done(String src) {
                String result=System.getProperty("user.name");
                Environment environment = templateContext.getEnvironment();
                if(null!=environment) {
                    String author = environment.getProperty("code.project.file.project-author");
                    result=Utils.isEmpty(author)?result:author;
                }
                return result;
            }
        },
        /**
         * 上一级路径 比如包名 com.zzd 的上一级就是 com,com/zzd 为 com
         */
        UP_LEVEL_PATH("upLevelPath"){
            @Override
            String done(String src) {
                String result="";
                if(src.indexOf(".")>0){
                    result=src.substring(0,src.lastIndexOf("."));
                }else if(src.indexOf("/")>0){
                    result=src.substring(0,src.lastIndexOf("/"));
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
                            stringBuilder.append(" \",").append(mendFirstStr);
                            a = 0;
                        }
                    }

                }
                return stringBuilder.toString();
            }
        }

        ;

        private final String value;
        public String getValue() {
            return value;
        }
        Function(String value) {
            this.value = value;
        }
        abstract String done(String src);
    }

    private static final String LANG_NAME="tool";
    private static final Pattern TEMPLATE_FUNCTION_BODY_PATTERN = Pattern.compile("(\\s*"+ AbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX+"\\s*"+LANG_NAME+"\\s*\\.(?<function>.*?)\\(\\s*(?<title>.*?)\\s*\\)\\s*"+AbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX+"\\s*)", Pattern.DOTALL);
    public static final Pattern TEMPLATE_GRAMMAR_PATTERN_SUFFIX = Pattern.compile("(\\s*"+LANG_NAME+"\\s*\\.(?<function>.*?)\\(\\s*(?<title>.*?)\\s*)", Pattern.DOTALL);
    private final Set<Pattern> excludeVariablePatten=new HashSet<>(Collections.singletonList(TEMPLATE_GRAMMAR_PATTERN_SUFFIX));

    public ToolTemplateLangResolver(TemplateResolver templateResolver) {
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
        tableInfo= (TableInfo) dataModal.get("tableInfo");
        Matcher matcher= TEMPLATE_FUNCTION_BODY_PATTERN.matcher(srcData);
        String result="";
        while(matcher.find()){
            String title = matcher.group("title");
            String function = matcher.group("function");
            String all=matcher.group(0);
            Set<String> variableKeySet=getTemplateResolver().getTemplateVariableKey(title);
            StringBuilder titleBuilder=new StringBuilder(title);
            for(String variableKey:variableKeySet){
                String realTitle;
                if(isMatchDependTemplate(variableKey)){
                    String finalTitle = titleBuilder.toString();
                    realTitle = getDependTemplateResolver().map(s->s.langResolver(finalTitle, dataModal)).orElse(finalTitle);
                }else {
                    Object objectTarget;
                    objectTarget = Utils.getTargetObject(dataModal, variableKey);
                    if (objectTarget instanceof String) {
                        realTitle = titleBuilder.toString().replaceAll(AbstractTemplateResolver.getTemplateVariableFormat(variableKey), (String) objectTarget);
                    } else {
                        realTitle = getLangBodyResult(objectTarget, titleBuilder.toString(), variableKey.split("\\.")[0]);
                    }
                }
                titleBuilder.setLength(0);
                titleBuilder.append(realTitle);
            }
            title=titleBuilder.length()==0?title:titleBuilder.toString();
            Function functionTemp=checkFunction(function);
            String mendLastStr = Utils.getLastNewLineNull(all);
            mendFirstStr=Utils.getFirstNewLineNull(all);
            String bodyResult=functionTemp.done(title);
            bodyResult=mendFirstStr+bodyResult+ mendLastStr;
            result = Utils.isEmpty(result) ? srcData.replace(all, bodyResult) : result.replace(all, bodyResult);
        }
        return Utils.isEmpty(result)?srcData:result;
    }

    public Set<String> getToolTemplateVariableKey(String src){
        Matcher matcher= TEMPLATE_FUNCTION_BODY_PATTERN.matcher(src);
        Set<String> set=new HashSet<>();
        while(matcher.find()){
            String title = matcher.group("title");
            Set<String> templateVariableKey = getTemplateResolver().getTemplateVariableKey(title);
            set.addAll(templateVariableKey);
        }
        return set;
    }

    private Optional<DependTemplateLangResolver> getDependTemplateResolver(){
        final AbstractTemplateResolver templateResolver = (AbstractTemplateResolver)getTemplateResolver();
        return Optional.ofNullable(templateResolver)
                .flatMap(b -> b.getTemplateLangResolverList()
                .stream().filter(s -> s instanceof DependTemplateLangResolver)
                .map(s -> (DependTemplateLangResolver) s)
                .findFirst());
    }

    private boolean isMatchDependTemplate(String title){
        Optional<DependTemplateLangResolver> templateResolver = getDependTemplateResolver();
        return templateResolver.map(dependTemplateResolver -> dependTemplateResolver.getExcludeVariablePatten()
                .stream()
                .anyMatch(s -> s.matcher(title).find()))
                .orElse(false);
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

}
