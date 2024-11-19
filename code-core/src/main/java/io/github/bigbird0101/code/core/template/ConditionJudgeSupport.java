package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import io.github.bigbird0101.code.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.text.StrPool.BRACKET_END;
import static cn.hutool.core.text.StrPool.BRACKET_START;
import static cn.hutool.core.text.StrPool.COMMA;
import static cn.hutool.core.text.StrPool.DOT;

/**
 *
 * @author bigbird-0101
 * @since 2023-04-10 09:52
 */
public class ConditionJudgeSupport {
    public static final Set<String> SPECIAL_SET = new LinkedHashSet<>(Arrays.asList("!=", "==", ">", "<", ">=", "<=", " in ",
            " not in ", " anyMatch ", " allMatch ", " noneMatch ", " contains ", " containsIgnoreCase "
            , " containsAnyIgnoreCase ", " containsAny ", " containsAll "));
    public static final Set<String> SPECIAL_SET_TRIM=SPECIAL_SET.stream().map(String::trim).collect(Collectors.toCollection(LinkedHashSet::new));

    public static final String AND_STRING = "&&";
    public static final String OR_STRING = "||";
    protected static final String PREFIX = "'";

    private final Map<String, Operator> operatorMap = Stream.of(new NotOperator(), new EqualsOperator(),
                    new NotEqualsOperator(), new InOperator(), new NotInOperator(), new AnyMatchOperator(),
                    new AllMatchOperator(), new NoneMatchOperator(), new ContainsOperator(),
                    new ContainsIgnoreCaseOperator(), new ContainsAnyOperator(), new ContainsAllOperator()
                    , new ContainsAnyIgnoreCaseOperator())
            .collect(Collectors.toMap(Operator::getOperatorName, s -> s));

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
     * @return 是否满足条件
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
     * @return 后缀表达式
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
                    Operator operator = operatorMap.get(special.trim());
                    if (null == operator) {
                        throw new TemplateResolveException("if 语句中" + content + " 运算符 " + special + " 错误,未找到其实现");
                    }
                    result.add(splitSuffix);
                    result.add(special.trim());
                    operator.checkExpression(content, splitPrefix, splitSuffix);
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
     * @return boolean
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
            } else {
                Operator operator = operatorMap.get(value);
                if (null == operator) {
                    throw new TemplateResolveException("if 语句中" + postfixExpressionModule.getTitle() + " 运算符 " + value + " 错误,未找到其实现");
                }
                operator.apply(stack, targetObjectMap);
            }
        }
        if(AND_STRING.equals(title)){
            return !stack.contains(Boolean.FALSE.toString());
        }else if(OR_STRING.equals(title)){
            return stack.contains(Boolean.TRUE.toString());
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
            result = Utils.getLatestObjectFieldValue(stringField, targetObject);
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

    private interface Operator extends Order {
        String getOperatorName();

        void apply(Stack<String> computeStack, Map<String, Object> targetObjectMap);

        /**
         * 校验表达式
         *
         * @param completedExpression 完整的表达式
         * @param left                左短语
         * @param right               右短语
         * @throws TemplateResolveException TemplateResolveException
         */
        default void checkExpression(String completedExpression, String left, String right) {
        }
    }

    private interface Order {
        /**
         * 优先级 越小越排在前面执行
         *
         * @return 优先级
         */
        default int getOrder() {
            return Integer.MAX_VALUE;
        }
    }

    private abstract class AbstractContainsMatchOperator extends AbstractMathchOperator {
    }

    private class ContainsAllOperator extends AbstractContainsMatchOperator {
        @Override
        boolean doMatch(List<String> leftStringList, List<String> rightStringList) {
            return leftStringList.stream().allMatch(s -> rightStringList.stream().allMatch(b -> StrUtil.contains(s, b)));
        }

        @Override
        public String getOperatorName() {
            return "containsAll";
        }
    }

    private class ContainsAnyOperator extends AbstractContainsMatchOperator {
        @Override
        boolean doMatch(List<String> leftStringList, List<String> rightStringList) {
            return leftStringList.stream().anyMatch(s -> rightStringList.stream().anyMatch(b -> StrUtil.contains(s, b)));
        }

        @Override
        public String getOperatorName() {
            return "containsAny";
        }
    }

    private class ContainsAnyIgnoreCaseOperator extends AbstractContainsMatchOperator {
        @Override
        boolean doMatch(List<String> leftStringList, List<String> rightStringList) {
            return leftStringList.stream().anyMatch(s -> rightStringList.stream().anyMatch(b -> StrUtil.containsIgnoreCase(s, b)));
        }

        @Override
        public String getOperatorName() {
            return "containsAnyIgnoreCase";
        }
    }

    private abstract class AbstractContainsOperator implements Operator {
        @Override
        public void apply(Stack<String> computeStack, Map<String, Object> targetObjectMap) {
            String right = computeStack.pop();
            String left = computeStack.pop();
            Object leftObj = getRealObject(getTargetObject(targetObjectMap, left), left);
            Object tempRight = getRealObject(getTargetObject(targetObjectMap, right), right);
            String leftString = StrUtil.strip(String.valueOf(leftObj), PREFIX, PREFIX);
            String rightString = StrUtil.strip(String.valueOf(tempRight), PREFIX, PREFIX);
            boolean result = doContains(leftString, rightString);
            computeStack.push(String.valueOf(result));
        }

        abstract boolean doContains(String leftString, String rightString);

        @Override
        public void checkExpression(String completedExpression, String left, String right) {
            if (StrUtil.isAllNotBlank(left, right)) {
                if (StrUtil.isSurround(right, "(", ")") || StrUtil.isSurround(right, BRACKET_START, BRACKET_END)) {
                    throw new TemplateResolveException("if 语句中 " + getOperatorName() + "语句错误 仅支持字符串之间的contains，但是右短语语句却传" + right);
                }
                if (StrUtil.isSurround(left, "(", ")") || StrUtil.isSurround(left, BRACKET_START, BRACKET_END)) {
                    throw new TemplateResolveException("if 语句中 " + getOperatorName() + "语句错误 仅支持字符串之间的contains，但是左短语语句却传" + left);
                }
            }
        }
    }

    private class ContainsOperator extends AbstractContainsOperator {
        @Override
        public String getOperatorName() {
            return "contains";
        }

        @Override
        boolean doContains(String leftString, String rightString) {
            return leftString.contains(rightString);
        }
    }

    private class ContainsIgnoreCaseOperator extends AbstractContainsOperator {
        @Override
        public String getOperatorName() {
            return "containsIgnoreCase";
        }

        @Override
        boolean doContains(String leftString, String rightString) {
            return StrUtil.containsIgnoreCase(leftString, rightString);
        }
    }

    private class NotOperator implements Operator {
        @Override
        public String getOperatorName() {
            return "!";
        }

        @Override
        public void apply(Stack<String> computeStack, Map<String, Object> targetObjectMap) {
            String filed = computeStack.pop();
            Object temp = getRealObject(getTargetObject(targetObjectMap, filed), filed);
            if (temp instanceof Boolean) {
                computeStack.push(String.valueOf(!(Boolean) temp));
            } else if (temp instanceof String) {
                computeStack.push(String.valueOf(!Boolean.parseBoolean((String) temp)));
            }
        }

        @Override
        public int getOrder() {
            return 1;
        }
    }

    private class EqualsOperator implements Operator {
        @Override
        public String getOperatorName() {
            return "==";
        }

        @Override
        public void apply(Stack<String> computeStack, Map<String, Object> targetObjectMap) {
            String right = computeStack.pop();
            String left = computeStack.pop();
            Object rightObj = getRealObject(getTargetObject(targetObjectMap, right), right);
            Object leftObj = getRealObject(getTargetObject(targetObjectMap, left), left);
            if (String.valueOf(rightObj).equals(String.valueOf(leftObj))) {
                computeStack.push(Boolean.TRUE.toString());
            } else {
                computeStack.push(Boolean.FALSE.toString());
            }
        }

        @Override
        public int getOrder() {
            return 2;
        }
    }

    private class NotEqualsOperator implements Operator {

        @Override
        public String getOperatorName() {
            return "!=";
        }

        @Override
        public void apply(Stack<String> computeStack, Map<String, Object> targetObjectMap) {
            String right = computeStack.pop();
            String left = computeStack.pop();
            Object rightObj = getRealObject(getTargetObject(targetObjectMap, right), right);
            Object leftObj = getRealObject(getTargetObject(targetObjectMap, left), left);
            if (String.valueOf(rightObj).equals(String.valueOf(leftObj))) {
                computeStack.push(Boolean.FALSE.toString());
            } else {
                computeStack.push(Boolean.TRUE.toString());
            }
        }

        @Override
        public int getOrder() {
            return 2;
        }
    }

    private class NotInOperator implements Operator {
        @Override
        public String getOperatorName() {
            return "not in";
        }

        @Override
        public void apply(Stack<String> computeStack, Map<String, Object> targetObjectMap) {
            String right = computeStack.pop();
            String left = computeStack.pop().trim();
            if (StrUtil.isSurround(right, BRACKET_START, BRACKET_END)) {
                Object leftObj = getRealObject(getTargetObject(targetObjectMap, left), left);
                String strip = StrUtil.strip(right, BRACKET_START, BRACKET_END);
                computeStack.push(String.valueOf(StrUtil.split(strip, COMMA)
                        .stream()
                        .map(s -> StrUtil.strip(s.trim(), PREFIX, PREFIX))
                        .noneMatch(s -> s.equals(String.valueOf(leftObj)))));
            } else {
                Object rightObj = getRealObject(getTargetObject(targetObjectMap, right), right);
                Object leftObj = getRealObject(getTargetObject(targetObjectMap, left), left);
                if (rightObj instanceof Collection) {
                    Collection<?> temp1Collection = (Collection<?>) rightObj;
                    boolean anyMatch = temp1Collection.stream()
                            .map(s -> StrUtil.strip(String.valueOf(s).trim(), PREFIX, PREFIX))
                            .noneMatch(s -> s.equals(String.valueOf(leftObj)));
                    computeStack.push(String.valueOf(anyMatch));
                } else {
                    computeStack.push(String.valueOf(!StrUtil.contains(String.valueOf(rightObj), String.valueOf(leftObj))));
                }
            }
        }

        @Override
        public int getOrder() {
            return 3;
        }
    }

    private class InOperator implements Operator {
        @Override
        public String getOperatorName() {
            return "in";
        }

        @Override
        public void apply(Stack<String> computeStack, Map<String, Object> targetObjectMap) {
            String right = computeStack.pop();
            String left = computeStack.pop().trim();
            if (StrUtil.isSurround(right, BRACKET_START, BRACKET_END)) {
                Object leftObj = getRealObject(getTargetObject(targetObjectMap, left), left);
                String strip = StrUtil.strip(right, BRACKET_START, BRACKET_END);
                computeStack.push(String.valueOf(StrUtil.split(strip, COMMA)
                        .stream()
                        .map(s -> StrUtil.strip(s.trim(), PREFIX, PREFIX))
                        .anyMatch(s -> s.equals(String.valueOf(leftObj)))));
            } else {
                Object leftObj = getRealObject(getTargetObject(targetObjectMap, left), left);
                Object rightObj = getRealObject(getTargetObject(targetObjectMap, right), right);
                if (rightObj instanceof Collection) {
                    Collection<?> rightCollection = (Collection<?>) rightObj;
                    boolean anyMatch = rightCollection.stream()
                            .map(s -> StrUtil.strip(String.valueOf(s).trim(), PREFIX, PREFIX))
                            .anyMatch(s -> s.equals(String.valueOf(leftObj)));
                    computeStack.push(String.valueOf(anyMatch));
                } else {
                    computeStack.push(String.valueOf(StrUtil.contains(String.valueOf(rightObj), String.valueOf(leftObj))));
                }
            }
        }

        @Override
        public int getOrder() {
            return 4;
        }
    }

    private abstract class AbstractMathchOperator implements Operator {
        @Override
        public void apply(Stack<String> computeStack, Map<String, Object> targetObjectMap) {
            String right = computeStack.pop();
            String left = computeStack.pop();
            Object leftObj = getRealObject(getTargetObject(targetObjectMap, left), left);
            if (StrUtil.isSurround(right, "(", ")")) {
                String strip = StrUtil.strip(right, "(", ")");
                List<String> split = StrUtil.splitTrim(strip, COMMA, 2);
                if (split.size() == 1) {
                    String rightFirstParamString = split.get(0);
                    if (StrUtil.isSurround(rightFirstParamString, BRACKET_START, BRACKET_END)) {
                        List<String> rightStringList = StrUtil.split(rightFirstParamString, COMMA)
                                .stream()
                                .map(s -> StrUtil.strip(s.trim(), PREFIX, PREFIX))
                                .collect(Collectors.toList());
                        if (leftObj instanceof Collection) {
                            List<String> leftStringList = ((Collection<?>) leftObj).stream().map(Object::toString)
                                    .collect(Collectors.toList());
                            boolean result = doMatch(leftStringList, rightStringList);
                            computeStack.push(String.valueOf(result));
                        } else {
                            String leftObjString = leftObj.toString();
                            boolean result = doMatch(Collections.singletonList(leftObjString), rightStringList);
                            computeStack.push(String.valueOf(result));
                        }
                    } else {
                        if (leftObj instanceof Collection) {
                            List<String> leftStringList = ((Collection<?>) leftObj).stream().map(Object::toString)
                                    .collect(Collectors.toList());
                            boolean result = doMatch(leftStringList, Collections.singletonList(rightFirstParamString));
                            computeStack.push(String.valueOf(result));
                        } else {
                            String leftObjString = leftObj.toString();
                            boolean doMatchResult = doMatch(Collections.singletonList(leftObjString),
                                    Collections.singletonList(rightFirstParamString));
                            computeStack.push(String.valueOf(doMatchResult));
                        }
                    }
                } else if (split.size() == 2) {
                    String rightFirstParamString = StrUtil.strip(split.get(0).trim(), PREFIX, PREFIX);
                    if (StrUtil.isBlank(rightFirstParamString)) {
                        throw new TemplateResolveException("if 语句中 " + getOperatorName() + "语句错误 左短语【" + left + "】不是数组类型");
                    }
                    String rightSecondParamString = split.get(1);
                    if (!(leftObj instanceof Collection)) {
                        throw new TemplateResolveException("if 语句中 " + getOperatorName() + "语句错误 左短语【" + left + "】不是数组类型");
                    }
                    Collection<?> leftCollection = (Collection<?>) leftObj;
                    List<String> leftStringList = leftCollection.stream()
                            .map(s -> {
                                if ((s instanceof Number || s instanceof Character)) {
                                    throw new TemplateResolveException("if 语句中 " + getOperatorName() +
                                            "语句错误 左短语【" + left + "】数组里面的对象是基础对象，无法获取属性【"
                                            + rightFirstParamString + "】");
                                }
                                return String.valueOf(getRealObject(s, rightFirstParamString)).trim();
                            })
                            .collect(Collectors.toList());
                    List<String> rightStringList;
                    if (StrUtil.isSurround(rightSecondParamString, BRACKET_START, BRACKET_END)) {
                        String stripRightSecondParamString = StrUtil.strip(rightSecondParamString, BRACKET_START, BRACKET_END);
                        rightStringList = StrUtil.split(stripRightSecondParamString, COMMA)
                                .stream()
                                .map(s -> StrUtil.strip(s.trim(), PREFIX, PREFIX))
                                .collect(Collectors.toList());
                    } else {
                        rightStringList = Collections.singletonList(rightSecondParamString);
                    }
                    boolean doMatchResult = doMatch(leftStringList, rightStringList);
                    computeStack.push(String.valueOf(doMatchResult));
                }
            } else if (StrUtil.isSurround(right, BRACKET_START, BRACKET_END)) {
                String strip = StrUtil.strip(right, BRACKET_START, BRACKET_END);
                Stream<String> rightRealStringList = StrUtil.split(strip, COMMA)
                        .stream()
                        .map(s -> StrUtil.strip(s.trim(), PREFIX, PREFIX));
                if (leftObj instanceof Collection) {
                    List<String> leftStringList = ((Collection<?>) leftObj).stream().map(Object::toString)
                            .collect(Collectors.toList());
                    List<String> rightStringList = rightRealStringList
                            .collect(Collectors.toList());
                    boolean result = doMatch(leftStringList, rightStringList);
                    computeStack.push(String.valueOf(result));
                } else {
                    String leftObjString = leftObj.toString();
                    boolean result = doMatch(Collections.singletonList(leftObjString),
                            rightRealStringList.collect(Collectors.toList()));
                    computeStack.push(String.valueOf(result));
                }
            }
        }

        abstract boolean doMatch(List<String> leftStringList, List<String> rightStringList);

        @Override
        public void checkExpression(String completedExpression, String left, String right) {
            if (StrUtil.isNotBlank(right)) {
                if (StrUtil.isSurround(right, "(", ")")) {
                    String strip = StrUtil.strip(right, "(", ")");
                    List<String> split = StrUtil.splitTrim(strip, COMMA, 2);
                    if (split.size() > 2) {
                        throw new TemplateResolveException("if 语句中 " + getOperatorName() + "语句错误 仅支持传两个参数，但是语句却传" + right);
                    }
                }
            }
        }
    }

    private class NoneMatchOperator extends AbstractMathchOperator {
        @Override
        public String getOperatorName() {
            return "noneMatch";
        }

        @Override
        public boolean doMatch(List<String> leftStringList, List<String> rightStringList) {
            return leftStringList.stream().noneMatch(rightStringList::contains);
        }
    }

    private class AllMatchOperator extends AbstractMathchOperator {
        @Override
        public String getOperatorName() {
            return "allMatch";
        }

        @Override
        public boolean doMatch(List<String> leftStringList, List<String> rightStringList) {
            return new HashSet<>(rightStringList).containsAll(leftStringList);
        }
    }

    private class AnyMatchOperator extends AbstractMathchOperator {
        @Override
        public String getOperatorName() {
            return "anyMatch";
        }

        @Override
        public boolean doMatch(List<String> leftStringList, List<String> rightStringList) {
            return leftStringList.stream().anyMatch(rightStringList::contains);
        }
    }

    private class PostfixExpressionModule {
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
