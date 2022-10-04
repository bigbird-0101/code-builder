package com.fpp.code.core.template.languagenode;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.fpp.code.exception.TemplateResolveException;
import com.fpp.code.util.Utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 22:44:29
 */
public class IfCodeNode implements CodeNode{
    public static final Set<String> SPECIAL_SET =new HashSet<>(Arrays.asList("!=","==",">","<",">=","<="));
    public static final String AND_STRING = "&&";
    public static final String OR_STRING = "||";

    private final String test;

    private final CodeNode contents;

    public IfCodeNode(String test, CodeNode contents) {
        this.test = test;
        this.contents = contents;
    }

    @Override
    public boolean apply(CodeNodeContext context) {
        List<String> targetKeyList=checkFiled(context.getTemplateVariable(),test);
        if(meetConditions(test,targetKeyList,context.getTemplateVariable())) {
            contents.apply(context);
            return true;
        }
        return false;
    }

    /**
     * 是否满足条件
     * @param test if条件
     * @param targetKeyList 条件中所包含的对象key
     * @param replaceKeyValue 语法中变量需要的对象kV
     * @return
     */
    private boolean meetConditions(String test, List<String> targetKeyList, Map<String, Object> replaceKeyValue) {
        for (String targetKey:targetKeyList){
            Object targetObject=replaceKeyValue.get(targetKey);
            StaticLog.info(" if语句满足条件目标对象 {}",targetObject);
            if(null==targetObject){
                throw new TemplateResolveException("if 语句中 由于{}源对象为空 不能解析条件语句 {}",targetKey,test);
            }
            return computeIfPostfixExpression(getIfPostfixExpression(test),targetObject);
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
            throw new TemplateResolveException("if 语句中"+langTitle+"，在变量表中未找到对应属性");
        }
        return targetKey;
    }

    /**
     * 获取if title的后缀表达式
     * @param str if语句的语法体 title
     * @return
     */
    public static PostfixExpressionModule getIfPostfixExpression(String str){
        List<String> result=new ArrayList<>();
        List<String> tempArray;
        String title=null;
        if(str.contains(AND_STRING)) {
            tempArray = Stream.of(str.split(AND_STRING)).filter(StrUtil::isNotBlank).collect(Collectors.toList());
            title=AND_STRING;
        }else if (str.contains(OR_STRING)){
            tempArray = Stream.of(str.split("\\||")).filter(StrUtil::isNotBlank).collect(Collectors.toList());
            title=OR_STRING;
        }else{
            tempArray = Stream.of(str.split(AND_STRING)).filter(StrUtil::isNotBlank).collect(Collectors.toList());
        }
        for (String content:tempArray) {
            //查看第一个是不是!不等于号
            boolean hasSpecial= content.trim().charAt(0) == '!';
            content=hasSpecial?content.substring(1):content;
            Set<String> tempSet = new HashSet<>();
            for (String special : SPECIAL_SET) {
                if (content.indexOf(special) > 0) {
                    if (tempSet.contains(content)) {
                        throw new TemplateResolveException(str + "语法错误");
                    }
                    tempSet.add(content);
                    String[] splitArray = content.split(special);
                    if (splitArray.length!=2) {
                        throw new TemplateResolveException(str + "语法错误");
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
        StaticLog.info(" if语句解析后的结果 {}",result.toString());
        return new PostfixExpressionModule(result,title);
    }

    /**
     * 计算后缀表达式
     * @param postfixExpressionModule 后缀表达式模型
     * @param targetObject 目标对象
     * @return
     */
    public  boolean computeIfPostfixExpression(PostfixExpressionModule postfixExpressionModule, Object targetObject){
        Stack<String> stack=new Stack<>();
        List<String> postfixExpression = postfixExpressionModule.getPostfixExpression();
        String title = postfixExpressionModule.getTitle();
        for(String value:postfixExpression){
            if(!SPECIAL_SET.contains(value)&&!value.contains("!")){
                stack.push(value);
            }else if("!".equals(value)){
                String filed=stack.pop();
                Object temp;
                temp = Utils.getObjectFieldValue(filed,targetObject);
                if(temp instanceof Boolean){
                    stack.push(String.valueOf(!(Boolean)temp));
                }
            }else if("==".equals(value)||"!=".equals(value)){
                String value1=stack.pop();
                String value3=stack.pop();
                Object temp;
                Object temp3;
                if(!value1.startsWith("'")&&!value1.endsWith("'")) {
                    temp = Utils.getObjectFieldValue(value1, targetObject);
                    temp = null == temp ? value1 : temp;
                }else{
                    temp= StrUtil.removeSuffix(StrUtil.removePrefix(value1,"'"),"'");
                }
                if(!value3.startsWith("'")&&!value3.endsWith("'")) {
                    temp3 = Utils.getObjectFieldValue(value3, targetObject);
                    temp3 = null == temp3 ? value3 : temp3;
                }else{
                    temp3= StrUtil.removeSuffix(StrUtil.removePrefix(value3,"'"),"'");
                }
                if (String.valueOf(temp).equals(String.valueOf(temp3))) {
                    stack.push(String.valueOf("==".equals(value)));
                } else {
                    stack.push(String.valueOf(!"==".equals(value)));
                }
            }
        }
        if(AND_STRING.equals(title)){
            return !stack.contains("false");
        }else if(OR_STRING.equals(title)){
            return stack.contains("true");
        }
        return Boolean.parseBoolean(stack.pop());
    }


    private static class PostfixExpressionModule{
        private List<String> postfixExpression;
        //目前支持&& || 不支持 混合使用
        private String title;

        public List<String> getPostfixExpression() {
            return postfixExpression;
        }

        public void setPostfixExpression(List<String> postfixExpression) {
            this.postfixExpression = postfixExpression;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        PostfixExpressionModule() {
        }

        PostfixExpressionModule(List<String> postfixExpression, String title) {
            this.postfixExpression = postfixExpression;
            this.title = title;
        }
    }
}
