package com.fpp.code.core.template;

import com.fpp.code.common.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/11 18:42
 */
public class IfTemplateResolver extends AbstractTemplateLangResolver{
    private static Logger logger= LogManager.getLogger(IfTemplateResolver.class);

    private static final String LANG_NAME="if";
    /**
     * 语法语句中的运算符
     */
    public final static Set<String> specialSet=new HashSet<>(Arrays.asList("!=","==",">","<",">=","<="));

    private Set<Pattern> excludeVariablePatten=new HashSet<>(Arrays.asList(templateGrammarPatternPrefix,templateGrammarPatternSuffix));

    public IfTemplateResolver() {
        super();
        this.resolverName=LANG_NAME;
    }

    public IfTemplateResolver(TemplateResolver template) {
        super(template);
        this.resolverName=LANG_NAME;
    }

    private static final Pattern templateFunctionBodyPattern= Pattern.compile("(\\s*?)(" + AbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX + "\\s*"+LANG_NAME+"\\s*v-if=[\"|\'](?<title>.*?)[\"|\']" + AbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX + ")(?<body>.*?)(" + AbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX + "\\s*/"+LANG_NAME+"\\s*" + AbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX + ")(\\s*?)", Pattern.DOTALL);
    private static final Pattern templateGrammarPatternPrefix= Pattern.compile("(\\s*"+LANG_NAME+"\\s*v-if=[\"|\'](?<title>.*?)[\"|\'])", Pattern.DOTALL);
    private static final Pattern templateGrammarPatternSuffix= Pattern.compile("(\\s*/"+LANG_NAME+"\\s*)", Pattern.DOTALL);

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
            String body = matcher.group("body");
            String title = matcher.group("title");
            String all=matcher.group(0);
            try {
                //是否满足if条件
                String temp = Utils.getFirstNewLineNull(body);
                String bodyResult = Boolean.parseBoolean(title) ? temp + body.trim() : "";
                result = Utils.isEmpty(result) ? srcData.replace(all, bodyResult) : result.replace(all, bodyResult);
            }catch(Exception e) {
                List<String> targetKeyList=checkFiled(replaceKeyValue,title);
                if(meetConditions(title,targetKeyList,replaceKeyValue)) {
                    for (String targetKey : targetKeyList) {
                        String bodyResult = getLangBodyResult(replaceKeyValue.get(targetKey), body, targetKey);
                        result = Utils.isEmpty(result) ? srcData.replace(all, bodyResult) : result.replace(all, bodyResult);
                    }
                }
            }
        }
        return Utils.isEmpty(result)?srcData:result;
    }
    /**
     * 是否满足条件
     * @param title if条件
     * @param targetKeyList 条件中所包含的对象key
     * @param replaceKeyValue 语法中变量需要的对象kV
     * @return
     */
    private boolean meetConditions(String title, List<String> targetKeyList, Map<String, Object> replaceKeyValue) {
        for (String targetKey:targetKeyList){
            Object targetObject=replaceKeyValue.get(targetKey);
            if(logger.isInfoEnabled()){
                logger.info(" if语句满足条件目标对象 {}",targetObject.toString());
            }
            return computeIfPostfixExpression(getIfPostfixExpression(title),targetObject);
        }
        return false;
    }


    private List<String> checkFiled(Map<String, Object> replaceKeyValue, String langTitle) {
        Set<String> keySet=replaceKeyValue.keySet();
        List<String> targetKey = new ArrayList<>(10);
        for(String key:keySet){
            if(langTitle.contains(key)){
                targetKey.add(key);
            }
        }
        if(targetKey.isEmpty()){
            throw new IllegalStateException(LANG_NAME+"语句中"+langTitle+"属性不存在");
        }
        return targetKey;
    }

    /**
     * 获取if title的后缀表达式
     * @param str if语句的语法体 title
     * @return
     */
    public static List<String> getIfPostfixExpression(String str){
        List<String> result=new ArrayList<>();
        String[] tempArray=str.split("&&");
        for (String content:tempArray) {
            boolean hasSpecial=content.contains("!");
            content=hasSpecial?content.substring(1):content;
            Set<String> tempSet = new HashSet<>();
            for (String special : specialSet) {
                if (content.indexOf(special) > 0) {
                    if (tempSet.contains(content)) {
                        throw new IllegalArgumentException(str + "语法错误");
                    }
                    tempSet.add(content);
                    String[] splitArray = content.split(special);
                    if (splitArray.length!=2) {
                        throw new IllegalArgumentException(str + "语法错误");
                    }
                    result.add(splitArray[0]);
                    if(hasSpecial) {
                        result.add("!");
                    }
                    result.add(splitArray[1]);
                    result.add(special);
                }
            }
            if (tempSet.isEmpty()) {
                result.add(content);
                if(hasSpecial) {
                    result.add("!");
                }

            }
        }
        if(logger.isInfoEnabled()){
            logger.info(" if语句解析后的结果 {}",result.toString());
        }
        return result;
    }

    /**
     * 计算后缀表达式
     * @param postfixExpression 后缀表达式
     * @param targetObject 目标对象
     * @return
     */
    public  boolean computeIfPostfixExpression(List<String> postfixExpression,Object targetObject){
        Stack<String> stack=new Stack<>();
        for(String value:postfixExpression){
            if(!specialSet.contains(value)&&!value.contains("!")){
                stack.push(value);
            }else if(value.contains("!")){
                String filed=stack.pop();
                Object temp;
                try {
                    temp = Utils.getObjectFieldValue(filed,targetObject);
                    if(temp instanceof Boolean){
                        stack.push(String.valueOf(!(Boolean)temp));
                    }
                } catch (TemplateResolveException e) {
                    stack.push(filed);
                }
            }else if("==".equals(value)){
                String value1=stack.pop();
                String value3=stack.pop();
                Object temp;
                Object temp3;
                try {
                    temp = Utils.getObjectFieldValue(value1,targetObject);
                } catch (TemplateResolveException e) {
                    temp=value1;
                }
                try{
                    temp3 = Utils.getObjectFieldValue(value3,targetObject);
                } catch (TemplateResolveException e) {
                    temp3=value3;
                }
                temp=null==temp?value1:temp;
                temp3=null==temp3?value3:temp3;
                if(temp.equals(temp3)){
                    stack.push(String.valueOf(true));
                }else{
                    stack.push(String.valueOf(false));
                }
            }
        }
        return Boolean.parseBoolean(stack.pop());
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
