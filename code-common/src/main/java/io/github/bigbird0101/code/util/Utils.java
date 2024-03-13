package io.github.bigbird0101.code.util;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.SPACE;
import static cn.hutool.core.text.CharSequenceUtil.lowerFirst;
import static java.util.stream.Collectors.toList;

/**
 * @author Administrator
 */
public abstract class Utils {
    private static final Logger LOGGER = LogManager.getLogger(Utils.class);

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isEmpty(Set<?> set) {
        return set == null || set.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String convertTruePathIfNotNull(String source) {
        if (isNotEmpty(source)) {
            return source.replaceAll("//", "/").replaceAll("\\\\", "/");
        }
        return source;
    }

    /**
     * 路径转包
     * @param path 路径
     * @return 路径转包
     */
    public static String pathToPackage(String path){
        return path.replaceAll("/",".")
                .replaceAll("//",".").replaceAll("\\\\",".");
    }

    /**
     * 第一个字符串大写
     * @param str 字符串
     * @return 第一个字符串大写
     */
    public static String firstUpperCase(String str) {
        if (str == null) {
            return "";
        }

        if (str.length() == 1) {
            str = str.toUpperCase();
        } else {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }

    /**
     * 第一个字符串小写
     * @param str 字符串
     * @return 第一个字符串小写
     */
    public static String firstLowerCase(String str) {
        if (str == null) {
            return "";
        }

        if (str.length() == 1) {
            str = str.toLowerCase();
        } else {
            str = str.substring(0, 1).toLowerCase() + str.substring(1);
        }
        return str;
    }

    public static String getFirstLowerCaseAndSplitLine(String str) {

        List<String> list = Arrays.stream(str.split("_")).collect(toList());
        int a = 0;
        StringBuilder result = new StringBuilder();
        for (String s : list) {
            if (a == 0) {
                result.append(firstLowerCase(s));
            } else {
                result.append(firstUpperCase(s));
            }
            a++;
        }
        return result.toString();
    }


    /**
     * 获取字符串的第一个换行和空格
     *
     * @param str 字符串
     * @return 获取字符串的第一个换行和空格
     */
    public static String getFirstNewLineNull(String str) {
        try {
            String first = str.trim().substring(0, 1);
            return str.substring(0, str.indexOf(first));
        }catch (StringIndexOutOfBoundsException e){
            return str;
        }
    }

    /**
     * 获取字符串的最后一个换行和空格
     *
     * @param str 字符串
     * @return 获取字符串的最后一个换行和空格
     */
    public static String getLastNewLineNull(String str) {
        String first = str.trim().substring(str.trim().length() - 1);
        return str.substring(str.lastIndexOf(first) + 1);
    }

    /**
     * 删除字符串尾部的空格
     * @param str 字符串
     * @return 删除字符串尾部的空格
     */
    public static String removeSuffixEmpty(String str){
        while(str.endsWith(SPACE)){
            str=StrUtil.removeSuffix(str, SPACE);
        }
        return str;
    }

    /**
     * 获取一个对象的属性字段值
     *
     * @param typeStr 一个对象的形如json的字符串属性列  比如:student.teacher.name
     * @param object  源对象  比如:student
     * @return 获取一个对象的属性字段值
     */
    @SuppressWarnings("unchecked")
    public static Object getObjectFieldValue(String typeStr, Object object) {
        String[] typeArray = typeStr.split("\\.");
        List<String> list = Arrays.stream(typeArray).filter(Utils::isNotEmpty).skip(1).collect(toList());
        Class<?> currentClazz= object.getClass();
        Object currentObj=object;
        Field currentField;
        for (final String name : list) {
            if (currentObj instanceof Map) {
                Map<String,Object> objMap = (Map<String,Object>) currentObj;
                currentObj = objMap.get(name);
                if(null==currentObj){
                    throw new TemplateResolveException("未定义的属性,模板中语法异常 {} 属性在对象[}中不存在",name,
                            lowerFirst(objMap.getClass().getSimpleName()));
                }
            } else {
                try {
                    currentField = ReflectUtil.getField(currentClazz, name);
                    if(null==currentField){
                        throw new TemplateResolveException("未定义的属性,模板中语法异常 {} 属性在对象{}中不存在",name,
                                lowerFirst(currentObj.getClass().getSimpleName()));
                    }
                    currentClazz = currentField.getClass();
                    currentObj = ReflectUtil.getFieldValue(currentObj, currentField);
                } catch (UtilException e) {
                    LOGGER.error("getObjectFieldValue error", e);
                    LOGGER.warn("getObjectFieldValue typeStr 对象{}中{}属性不存在",
                            lowerFirst(currentObj.getClass().getSimpleName()), name);
                    throw new TemplateResolveException("未定义的属性,模板中语法异常 {} 属性在对象{}中不存在",name,
                            lowerFirst(currentObj.getClass().getSimpleName()));
                }
            }
        }
        return currentObj;
    }

    /**
     * 校验字段是否存在,并返回最终的最后的对象 a.b.c 返回a对象中的b对象中的c对象
     *
     * @param replaceKeyValue 变量
     * @param itemParentNode  v-for( a in b) 中的 b
     * @return 对象 a.b.c 返回a对象中的b对象中的c对象
     */
    @SuppressWarnings("unchecked")
    public static Object getTargetObject(Map<String, Object> replaceKeyValue, String itemParentNode) {
        Object current = null;
        List<String> itemParentNodeList = Arrays.stream(itemParentNode.split("\\.")).filter(Utils::isNotEmpty)
                .map(String::trim).collect(toList());
        if (itemParentNodeList.isEmpty()) {
            throw new TemplateResolveException("模板中语法异常");
        }
        for (int i = 0; i < itemParentNodeList.size(); i++) {
            String nodeName = itemParentNodeList.get(i);
            if (i == 0) {
                if (!replaceKeyValue.containsKey(nodeName)) {
                    LOGGER.warn("getTargetObject itemParentNode {} 模板中语法异常 {} 属性在变量表中{}不存在",itemParentNode,nodeName,replaceKeyValue.keySet());
                    throw new TemplateResolveException("未定义的属性,模板中语法异常 {} 属性在变量表中不存在",itemParentNode,nodeName);
                }
                current = replaceKeyValue.get(nodeName);
            } else {
                if(current instanceof Map){
                    Map<String,Object> objMap= (Map<String,Object>) current;
                    current=objMap.get(nodeName);
                }else {
                    try {
                        current = ReflectUtil.getFieldValue(current, nodeName);
                    }catch (Exception e){
                        LOGGER.error("getTargetObject error", e);
                        LOGGER.warn("getTargetObject itemParentNode 模板{}中语法异常 {} 对象{}属性不存在",itemParentNode,current, nodeName);
                    }
                }
            }
        }
        return current;
    }

    /**
     * 通过路径名获取文件名
     *
     * @param path 路径名
     * @param pattern pattern
     * @return 通过路径名获取文件名
     */
    public static String getFileNameByPath(String path, String pattern) {
        String[] split = path.split(pattern);
        return Arrays.stream(split).map(Utils::firstUpperCase).skip(split.length - 1).collect(Collectors.joining());
    }

    /**
     * 把以_分隔的列明转化为字段名,将大写的首字母变小写
     *
     * @param columnName 列名
     * @return String 字段名
     */
    public static String getFieldName(String columnName) {
        if (columnName == null) {
            return "";
        }

        StringBuilder fieldNameBuffer = new StringBuilder();

        boolean nextUpperCase = false;
        for (int i = 0; i < columnName.length(); i++) {
            char c = columnName.charAt(i);

            if (nextUpperCase) {
                fieldNameBuffer.append(columnName.substring(i, i + 1).toUpperCase());
            } else {
                fieldNameBuffer.append(c);
            }

            nextUpperCase = c == '_';
        }

        String fieldName = fieldNameBuffer.toString();
        fieldName = fieldName.replaceAll("_", "");

        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
        return fieldName;
    }

    private static final  String UNDERLINE = "_";

    /***
     * 下划线命名转为驼峰命名
     *
     * @param para 下划线命名的字符串
     * @return 下划线命名转为驼峰命名
     */
    public static String underlineToHump(String para) {
        if (null == para) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        String[] a = para.split(UNDERLINE);
        for (String s : a) {
            if (!para.contains(UNDERLINE)) {
				result.append(s.substring(0, 1).toUpperCase());
				result.append(s.substring(1).toLowerCase());
                continue;
            }
            if (result.length() == 0) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    public static Matcher getIgnoreLowerUpperMather(String src,String regex){
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL|Pattern.UNICODE_CASE).matcher(src);
    }

    public static boolean isLower(String str){
        return str.toLowerCase().equals(str);
    }

    public static boolean isUpper(String str){
        return str.toUpperCase().equals(str);
    }
}
