package com.fpp.code.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
public class Utils {
    private static Logger logger = LogManager.getLogger(Utils.class);

    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Set set) {
        if (set == null || 0==set.size()) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static <T> T setIfNull(T source, T defaultValue) {
        if (null == source || (source instanceof String && isEmpty((String) source))) {
            return defaultValue;
        } else {
            return source;
        }
    }

    public static String convertTruePathIfNotNull(String source) {
        if (isNotEmpty(source)) {
            return source.replaceAll("//", "/").replaceAll("\\\\", "/");
        }
        return source;
    }

    /**
     * 路径转包
     * @param path
     * @return
     */
    public static String pathToPackage(String path){
        return path.replaceAll("/",".")
                .replaceAll("//",".").replaceAll("\\\\",".");
    }


    public static String getSystemUpLoadFile(String state) {
        String os = System.getProperty("os.name");
        StringBuffer path = new StringBuffer();
        if (!os.toLowerCase().startsWith("win")) {
            path.append("/usr/ProjectManagerUploadFile/lastversion");
        } else {
            path.append("C:/usr/ProjectManagerUploadFile/lastversion");
        }
        if ("1".equals(state)) {
            path.append("/sit");
        } else if ("2".equals(state)) {
            path.append("/uat");
        } else if ("3".equals(state)) {
            path.append("/temp");
        }

        Calendar now = Calendar.getInstance();
        String year = String.valueOf(now.get(Calendar.YEAR));
        String month = String.valueOf(now.get(Calendar.MONTH) + 1);
        String day = String.valueOf(now.get(Calendar.DAY_OF_MONTH));
        path.append("/" + year + "/" + month + "/" + day);
        File file = new File(path.toString());
        mkDirs(file);
        if (!file.exists()) {
            file.setWritable(true, false);
            file.mkdirs();
        }
        return path.toString();
    }

    public static String getCurrentDate() {
        SimpleDateFormat aSimpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        Calendar now = Calendar.getInstance();
        return aSimpleDateFormat.format(now.getTime());
    }

    public static String getCurrentDateTimeFm() {
        SimpleDateFormat aSimpleDateFormat = new SimpleDateFormat("YYYY-MM-dd_HH_mm_ss");
        Calendar now = Calendar.getInstance();
        return aSimpleDateFormat.format(now.getTime());
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat aSimpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Calendar now = Calendar.getInstance();
        return aSimpleDateFormat.format(now.getTime());
    }

    public static boolean mkDirs(File file) {
        if (file != null) {
            File parentFile = file.getParentFile();
            if (parentFile != null) {
                if (!parentFile.exists()) {
                    parentFile.setWritable(true, false);
                    parentFile.mkdir();
                    return mkDirs(parentFile);
                }
            }
        }
        return true;
    }

    /**
     * 第一个字符串大写
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

        List<String> list = Arrays.stream(str.split("_")).collect(Collectors.toList());
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
     * @说明 ：格式化java代码
     * @参数 ：@param dataTmp
     * @参数 ：@return
     * @返回 ：String
     * @时间 ：2018 11 22
     **/
    public static String formJava(String data) {
        String dataTmp = replaceStrToUUid(data, "\"");
        dataTmp = replaceStrToUUid(dataTmp, "'");
        dataTmp = repalceHHF(dataTmp, "\n", "");
        dataTmp = repalceHHF(dataTmp, "{", "{\n");
        dataTmp = repalceHHF(dataTmp, "}", "}\n");
        dataTmp = repalceHHF(dataTmp, "/*", "\n/**\n");
        dataTmp = repalceHHF(dataTmp, "* @", "\n* @");
        dataTmp = repalceHHF(dataTmp, "*/", "\n*/\n");
        dataTmp = repalceHHF(dataTmp, ";", ";\n");
        dataTmp = repalceHHF(dataTmp, "//", "\n//");
        dataTmp = repalceHHFX(dataTmp, "\n");
        for (Map.Entry<String, String> r : mapZY.entrySet()) {
            dataTmp = dataTmp.replace(r.getKey(), r.getValue());
        }
        if (dataTmp == null) {
            return data;
        }
        return dataTmp;
    }

    public static Map<String, String> mapZY = new HashMap<String, String>();

    /**
     * @说明 ： 循环替换指定字符为随机uuid  并将uui存入全局map:mapZY
     * @参数 ：@param string   字符串
     * @参数 ：@param type    指定字符
     * @时间 ：2018 11 23
     **/
    private static String replaceStrToUUid(String string, String type) {
        Matcher slashMatcher = Pattern.compile(type).matcher(string);
        boolean bool = false;
        StringBuilder sb = new StringBuilder();
        //开始截取下标
        int indexHome = -1;
        while (slashMatcher.find()) {
            int indexEnd = slashMatcher.start();
            //获取"号前面的数据
            String tmp = string.substring(indexHome + 1, indexEnd);
            if (indexHome == -1 || !bool) {
                sb.append(tmp);
                bool = true;
                indexHome = indexEnd;
            } else {
                String tem2 = "";
                for (int i = indexEnd - 1; i > -1; i--) {
                    char c = string.charAt(i);
                    if (c == '\\') {
                        tem2 += c;
                    } else {
                        break;
                    }
                }
                int tem2Len = tem2.length();
                //结束符前有斜杠转义符 需要判断转义个数奇偶   奇数是转义了  偶数才算是结束符号
                if (tem2Len % 2 == 1) {
                    //奇数 非结束符
                } else {
                    //偶数才算是结束符号
                    String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                    uuid = type + uuid + type;
                    mapZY.put(uuid, type + tmp + type);
                    sb.append(uuid);
                    bool = false;
                    indexHome = indexEnd;
                }
            }
        }
        sb.append(string.substring(indexHome + 1));
        return sb.toString();
    }


    //处理换行
    private static String repalceHHF(String data, String a, String b) {
        try {
            data = data.replace(a, "$<<yunwangA>>$<<yunwangB>>");
            String[] arr = data.split("$<<yunwangA>>");
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < arr.length; i++) {
                String t = arr[i];
                result.append(t.trim());
                if (t.contains("//") && "\n".equals(a)) {
                    result.append("\n");
                }
            }
            String res = result.toString();
            res = res.replace("$<<yunwangB>>", b);
            res = res.replace("$<<yunwangA>>", "");
            return res;
        } catch (Exception e) {
        }
        return null;
    }

    //处理缩进
    private static String repalceHHFX(String data, String a) {
        try {
            String[] arr = data.split(a);
            StringBuilder result = new StringBuilder();
            String zbf = "    ";
            Stack<String> stack = new Stack<String>();
            for (int i = 0; i < arr.length; i++) {
                String tem = arr[i].trim();
                if (tem.contains("{")) {
                    String kg = getStack(stack, false);
                    if (kg == null) {
                        result.append(tem).append("\n");
                        kg = "";
                    } else {
                        kg = kg + zbf;
                        result.append(kg).append(tem).append("\n");
                    }
                    stack.push(kg);
                } else if (tem.contains("}")) {
                    String kg = getStack(stack, true);
                    if (kg == null) {
                        result.append(tem).append("\n");
                    } else {
                        result.append(kg).append(tem).append("\n");
                    }
                } else {
                    String kg = getStack(stack, false);
                    if (kg == null) {
                        result.append(tem).append("\n");
                    } else {
                        result.append(kg).append(zbf).append(tem).append("\n");
                    }
                }
            }
            String res = result.toString();
            return res;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * @说明 ： 获得栈数据
     * @参数 ：@param stack
     * @参数 ：@param bool true 弹出  false 获取
     **/
    private static String getStack(Stack<String> stack, boolean bool) {
        String result = null;
        try {
            if (bool) {
                return stack.pop();
            }
            return stack.peek();
        } catch (EmptyStackException e) {
        }
        return result;
    }

    /**
     * 获取字符串的第一个换行和空格
     *
     * @param str
     * @return
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
     * 替换时 更具src变量的不忽略大小写进行替换
     *
     * @param srcSource   源
     * @param srcVari     src变量
     * @param replaceVari 需要替换的值
     * @return
     */
    public static String replaceIngoreCase(String srcSource, String srcVari, String replaceVari) {
        return getIgnoreLowerUpperMather(srcSource,srcVari).replaceAll(replaceVari);
    }

    /**
     * 获取字符串的最后一个换行和空格
     *
     * @param str
     * @return
     */
    public static String getLastNewLineNull(String str) {
        String first = str.trim().substring(str.trim().length() - 1);
        return str.substring(str.lastIndexOf(first) + 1);
    }


    /**
     * 获取一个对象的属性字段值
     *
     * @param typeStr 一个对象的形如json的字符串属性列  比如:student.teacher.name
     * @param object  源对象  比如:student
     * @return
     */
    public static Object getObjectFieldValue(String typeStr, Object object) {
        Field temp = null;
        String[] typeArray = typeStr.split("\\.");
        List<String> list = Arrays.stream(typeArray).filter(Utils::isNotEmpty).skip(1).collect(Collectors.toList());
        Class<?> clazz = object.getClass();
        Class<?> tempClass;
        Object beforeTemp=object;
        for (int i = 0; i < list.size(); i++) {
            final String name = list.get(i);
            if(beforeTemp instanceof JSONObject){
                JSONObject jsonObject= (JSONObject) object;
                beforeTemp=jsonObject.get(name);
            }else{
                if (i == 0) {
                    try {
                        temp = clazz.getField(name);
                    } catch (NoSuchFieldException e) {
                        try {
                            temp = clazz.getDeclaredField(list.get(i));
                        } catch (NoSuchFieldException ex) {
                            logger.warn("对象{}中{}属性不存在", typeArray[0], list.get(i));
                        }
                    }
                    beforeTemp = temp;
                } else {
                    beforeTemp = temp;
                    tempClass = temp.getClass();
                    try {
                        temp = tempClass.getField(list.get(i));
                    } catch (NoSuchFieldException e) {
                        try {
                            temp = tempClass.getDeclaredField(list.get(i));
                        } catch (NoSuchFieldException ex) {
                            logger.warn("对象{}中{}属性不存在", list.get(i - 1), list.get(i));
                        }
                    }
                }
                if (i == list.size() - 1) {
                    Field field = temp;
                    try {
                        field.setAccessible(true);
                        return field.get(beforeTemp);
                    } catch (IllegalAccessException | NullPointerException exception) {
                        logger.warn("获取一个对象的属性字段值异常 {} {}", field, beforeTemp);
                    }
                }
            }
        }
        return beforeTemp;
    }

    /**
     * 校验字段是否存在,并返回最终的最后的对象 a.b.c 返回a对象中的b对象中的c对象
     *
     * @param replaceKeyValue
     * @param itemParentNode  v-for( a in b) 中的 b
     * @return 对象 a.b.c 返回a对象中的b对象中的c对象
     */
    public static Object getTargetObject(Map<String, Object> replaceKeyValue, String itemParentNode) throws IllegalAccessException {
        Object temp = null;
        List<String> itemParentNodeList = Arrays.stream(itemParentNode.split("\\.")).filter(Utils::isNotEmpty).map(String::trim).collect(Collectors.toList());
        if (itemParentNodeList.isEmpty()) {
            throw new IllegalArgumentException("模板中语法异常");
        }
        for (int i = 0; i < itemParentNodeList.size(); i++) {
            String nodeName = itemParentNodeList.get(i);
            if (i == 0) {
                if (!replaceKeyValue.containsKey(nodeName)) {
                    logger.warn("模板中语法异常{}属性不存在", nodeName);
                    return null;
                }
                temp = replaceKeyValue.get(nodeName);
            } else {
                if(temp instanceof JSONObject){
                    JSONObject jsonObject= (JSONObject) temp;
                    temp=jsonObject.get(nodeName);
                }else {
                    Field field = null;
                    try {
                        field = temp.getClass().getField(nodeName);
                    } catch (NoSuchFieldException e) {
                        try {
                            field = temp.getClass().getDeclaredField(nodeName);
                        } catch (NoSuchFieldException ex) {
                            logger.warn("模板中语法异常{}属性不存在", nodeName);
                        }
                    }
                    if (null == field) {
                        logger.warn("模板中语法异常{}属性不存在", nodeName);
                    }
                    assert field != null;
                    field.setAccessible(true);
                    try {
                        temp = field.get(temp);
                    } catch (IllegalAccessException e) {
                        throw e;
                    }
                }
            }
        }
        return temp;
    }

    /**
     * 校验jsonarray中的jsonObject中的key是否存在
     *
     * @param jsonArray
     * @param key
     * @return
     */
    public static boolean checkJsonArray(JSONArray jsonArray, String key) {
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            if (jsonObject.containsKey(key)) {
                return true;
            }
        }
        ;
        return false;
    }

    /**
     * 通过路径名获取文件名
     *
     * @param path
     * @return
     */
    public static String getFileNameByPath(String path, String pattern) {
        String[] split = path.split(pattern);
        return Arrays.stream(split).map(Utils::firstUpperCase).skip(split.length - 1).collect(Collectors.joining());
    }

    /**
     * 获取jsonarray中key为key的jsonObject
     *
     * @param jsonArray
     * @param key
     * @return
     */
    public static JSONObject getJsonObjByKey(JSONArray jsonArray, String key) {
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            if (jsonObject.containsKey(key)) {
                return jsonObject;
            }
        }
        ;
        return new JSONObject();
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

            if (c == '_') {
                nextUpperCase = true;
            } else {
                nextUpperCase = false;
            }
        }

        String fieldName = fieldNameBuffer.toString();
        fieldName = fieldName.replaceAll("_", "");

        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
        return fieldName;
    }

    /**
     * 获取jsonarray中key为key的JSONArray
     *
     * @param jsonArray
     * @param key
     * @return
     */
    public static JSONArray getJsonArrayByKey(JSONArray jsonArray, String key) {
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            if (jsonObject.containsKey(key)) {
                return jsonObject.getJSONArray(key);
            }
        }
        ;
        return new JSONArray();
    }

    /**
     * mather group 是否匹配的是小写
     *
     * @param matcher
     * @return
     */
    public static boolean matherGroupIsLower(Matcher matcher) {
        while (matcher.find()) {
            String group = matcher.group();
            if (group.equals(group.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private final static String UNDERLINE = "_";

    /***
     * 下划线命名转为驼峰命名
     *
     * @param para 下划线命名的字符串
     *
     */
    public static String underlineToHump(String para) {
        if (null == para) return "";
        StringBuilder result = new StringBuilder();
        String a[] = para.split(UNDERLINE);
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

    /***
     * 驼峰命名转为下划线命名
     *
     * @param para 驼峰命名的字符串
     *
     */
    public static String humpToUnderline(String para) {
        if (null == para) return "";
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;//定位
        if (!para.contains(UNDERLINE)) {
            for (int i = 0; i < para.length(); i++) {
                if (Character.isUpperCase(para.charAt(i))) {
                    sb.insert(i + temp, UNDERLINE);
                    temp += 1;
                }
            }
        }
        return sb.toString().toUpperCase();
    }

    public static Matcher getIgnoreLowerUpperMather(String src,String regex){
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.DOTALL|Pattern.UNICODE_CASE).matcher(src);
    }

    public static String addSlashDoubleWhenSpecialCharacter(String src){
        return src.replaceAll("\\@","\\\\@").replaceAll("\\*","\\\\*");
    }

    public static boolean isLower(String str){
        return str.toLowerCase().equals(str);
    }

    public static boolean isUpper(String str){
        return str.toUpperCase().equals(str);
    }

    public static void main(String[] args) throws IllegalAccessException, IOException {
//		AbstractTemplate ab=new ControllerFileTemplate("DoMainTemplate.txt");
//		AbstractTemplateLangResolver dd=new IfTemplateResolver(ab);
//		TableInfo.ColumnInfo a=new TableInfo.ColumnInfo("id", "id", "Integer", "id", false, 10, false, false, "INTEGER");
//		computeIfPostfixExpression(getIfPostfixExpression("!column.isNull==\"String\""),a,dd);
//		String a=" <<tool.firstUpper(id)>>\r\n   ";
//		String b=getLastNewLineNull(a);
//		System.out.println(b);
//        System.out.println(getFileNameByPath("com/aa/bb", "\\/"));
		System.out.println(underlineToHump("a23AB"));
    }

}
//