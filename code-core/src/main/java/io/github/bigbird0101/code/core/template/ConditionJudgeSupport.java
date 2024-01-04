package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import io.github.bigbird0101.code.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.*;

/**
 *
 * @author pengpeng_fu@infinova.com.cn
 * @date 2023-04-10 09:52
 */
public class ConditionJudgeSupport {
    public static final Set<String> SPECIAL_SET =new LinkedHashSet<>(Arrays.asList("!=","==",">","<",">=","<="," in "," not in "));
    public static final Set<String> SPECIAL_SET_TRIM=SPECIAL_SET.stream().map(String::trim).collect(Collectors.toCollection(LinkedHashSet::new));

    public static final String AND_STRING = "&&";
    public static final String OR_STRING = "||";
    protected static final String PREFIX = "'";

    public List<String> checkFiled(Map<String, Object> dataModal, String langTitle) {
        Set<String> keySet=dataModal.keySet();
        List<String> targetKey = new ArrayList<>(10);
        final PostfixExpressionModule ifPostfixExpression = getIfPostfixExpression(langTitle);
        List<String> postfixExpression = ifPostfixExpression.getPostfixExpression();
        for(String key:keySet){
            for(String postfix:postfixExpression){
                if(postfix.equals(key)){
                    targetKey.add(key);
                }else if(postfix.contains(DOT)&&StrUtil.split(postfix, DOT).contains(key)){
                    targetKey.add(key);
                }
            }
        }
        if(targetKey.isEmpty()){
            throw new TemplateResolveException("条件判断 语句中"+langTitle+"，在变量表中未找到对应属性");
        }
        return targetKey;
    }

    /**
     * 是否满足条件
     * @param test if条件
     * @param targetKeyList 条件中所包含的对象key
     * @param replaceKeyValue 语法中变量需要的对象kV
     * @return
     */
    public boolean meetConditions(String test, List<String> targetKeyList, Map<String, Object> replaceKeyValue) {
        Map<String,Object> objectMap=new HashMap<>();
        for (String targetKey:targetKeyList){
            Object targetObject=replaceKeyValue.get(targetKey);
            StaticLog.info(" if语句 {} 满足条件目标对象 {}",targetKey,targetObject);
            if(null==targetObject){
                throw new TemplateResolveException("if 语句中 由于{}源对象为空 不能解析条件语句 {}",targetKey,test);
            }
            objectMap.put(targetKey,targetObject);
        }
        return computeIfPostfixExpression(getIfPostfixExpression(test),objectMap);
    }

    /**
     * 获取if title的后缀表达式
     * @param str if语句的语法体 title
     * @return
     */
    private PostfixExpressionModule getIfPostfixExpression(String str){
        List<String> result=new ArrayList<>();
        List<String> tempArray;
        String title=null;
        if(str.contains(AND_STRING)) {
            tempArray = StrUtil.split(str,AND_STRING).stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
            title=AND_STRING;
        }else if (str.contains(OR_STRING)){
            tempArray = StrUtil.split(str,OR_STRING).stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
            title=OR_STRING;
        }else{
            tempArray = StrUtil.split(str,OR_STRING).stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
        }
        for (String content:tempArray) {
            //查看第一个是不是!不等于号
            boolean hasSpecial= content.trim().charAt(0) == '!';
            content=hasSpecial?content.substring(1):content;
            Set<String> tempSet = new HashSet<>();
            for (String special : SPECIAL_SET) {
                if (content.indexOf(special) > 0 && !content.contains("not" + special)) {
                    if (tempSet.contains(content)) {
                        throw new TemplateResolveException(str + "语法错误");
                    }
                    tempSet.add(content);
                    String[] splitArray = content.split(special);
                    if (splitArray.length!=2) {
                        throw new TemplateResolveException(str + "语法错误");
                    }
                    String splitPrefix = splitArray[0].trim();
                    if (splitPrefix.contains(StrUtil.SPACE)) {
                        throw new TemplateResolveException(str + "语法错误");
                    }
                    result.add(splitPrefix);
                    if(hasSpecial) {
                        result.add("!");
                    }
                    String splitSuffix = splitArray[1].trim();
                    if (splitSuffix.contains(StrUtil.SPACE)) {
                        throw new TemplateResolveException(str + "语法错误");
                    }
                    result.add(splitSuffix);
                    result.add(special.trim());
                }
            }
            if (tempSet.isEmpty()) {
                result.add(StrUtil.removePrefix(content.trim(),"!"));
                if(hasSpecial) {
                    result.add("!");
                }
            }
        }
        StaticLog.info(" if语句解析的后缀表达式的结果 {}",result.toString());
        return new PostfixExpressionModule(result,title);
    }

    /**
     * 计算后缀表达式
     * @param postfixExpressionModule 后缀表达式模型
     * @param targetObjectMap 目标对象
     * @return
     */
    private  boolean computeIfPostfixExpression(PostfixExpressionModule postfixExpressionModule, Map<String,Object> targetObjectMap){
        Stack<String> stack=new Stack<>();
        List<String> postfixExpression = postfixExpressionModule.getPostfixExpression();
        if(postfixExpression.size()==1){
            return Boolean.parseBoolean(String.valueOf(targetObjectMap.values().stream().findFirst().orElse(null)));
        }
        String title = postfixExpressionModule.getTitle();
        for(String value:postfixExpression){
            if(!SPECIAL_SET_TRIM.contains(value)&&!value.contains("!")){
                stack.push(value);
            }else if("!".equals(value)){
                String filed=stack.pop();
                Object temp=getRealObject(getTargetObject(targetObjectMap,filed), filed);
                if(temp instanceof Boolean){
                    stack.push(String.valueOf(!(Boolean)temp));
                }else if(temp instanceof String){
                    stack.push(String.valueOf(!Boolean.parseBoolean((String) temp)));
                }
            }else if("==".equals(value)||"!=".equals(value)){
                String value1=stack.pop();
                String value2=stack.pop();
                Object temp1 = getRealObject(getTargetObject(targetObjectMap,value1), value1);
                Object temp2 = getRealObject(getTargetObject(targetObjectMap,value2), value2);
                if (String.valueOf(temp1).equals(String.valueOf(temp2))) {
                    stack.push(String.valueOf("==".equals(value)));
                } else {
                    stack.push(String.valueOf(!"==".equals(value)));
                }
            }else if("not in".equals(value)){
                String value1=stack.pop();
                String value2=stack.pop().trim();
                if(StrUtil.isSurround(value1,BRACKET_START,BRACKET_END)){
                    Object temp2 = getRealObject(getTargetObject(targetObjectMap,value2), value2);
                    String strip = StrUtil.strip(value1, BRACKET_START, BRACKET_END);
                    stack.push(String.valueOf(StrUtil.split(strip,COMMA)
                            .stream()
                            .map(s-> StrUtil.strip(s,PREFIX,PREFIX))
                            .noneMatch(s->s.equals(String.valueOf(temp2)))));
                }else {
                    Object temp1 = getRealObject(getTargetObject(targetObjectMap,value1), value1);
                    Object temp2 = getRealObject(getTargetObject(targetObjectMap,value2), value2);
                    if(temp1 instanceof Collection){
                        Collection<?> temp1Collection= (Collection<?>) temp1;
                        boolean anyMatch = temp1Collection.stream()
                                .map(s -> StrUtil.strip(String.valueOf(s), PREFIX, PREFIX))
                                .noneMatch(s -> s.equals(String.valueOf(temp2)));
                        stack.push(String.valueOf(anyMatch));
                    }else {
                        stack.push(String.valueOf(!StrUtil.contains(String.valueOf(temp2), String.valueOf(temp1))));
                    }
                }
            }else if("in".equals(value)){
                String value1=stack.pop();
                String value2=stack.pop().trim();
                if(StrUtil.isSurround(value1,BRACKET_START,BRACKET_END)){
                    Object temp2 = getRealObject(getTargetObject(targetObjectMap,value2), value2);
                    String strip = StrUtil.strip(value1, BRACKET_START, BRACKET_END);
                    stack.push(String.valueOf(StrUtil.split(strip,COMMA)
                            .stream()
                            .map(s-> StrUtil.strip(s,PREFIX,PREFIX))
                            .anyMatch(s->s.equals(String.valueOf(temp2)))));
                }else {
                    Object temp2 = getRealObject(getTargetObject(targetObjectMap,value2), value2);
                    Object temp1 = getRealObject(getTargetObject(targetObjectMap,value1), value1);
                    if(temp1 instanceof Collection){
                        Collection<?> temp1Collection= (Collection<?>) temp1;
                        boolean anyMatch = temp1Collection.stream()
                                .map(s -> StrUtil.strip(String.valueOf(s), PREFIX, PREFIX))
                                .anyMatch(s -> s.equals(String.valueOf(temp2)));
                        stack.push(String.valueOf(anyMatch));
                    }else {
                        stack.push(String.valueOf(StrUtil.contains(String.valueOf(temp2), String.valueOf(temp1))));
                    }
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

    private Object getRealObject(Object targetObject, String stringField) {
        if(null==targetObject){
            return StrUtil.isSurround(stringField,PREFIX,PREFIX)?
                    StrUtil.strip(stringField.trim(), PREFIX, PREFIX):
                    stringField;
        }
        Object result;
        if(StrUtil.isSurround(stringField,PREFIX,PREFIX)){
            result= StrUtil.strip(stringField.trim(), PREFIX, PREFIX);
        }else{
            result = Utils.getObjectFieldValue(stringField, targetObject);
            result = null == result ? stringField : result;
        }
        return result;
    }

    private Object getTargetObject(Map<String,Object> targetObjMap,String field){
        return targetObjMap.getOrDefault(field,targetObjMap.entrySet()
                .stream()
                .filter(s->StrUtil.split(field,DOT).stream().anyMatch(b->b.equals(s.getKey())))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null));
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
