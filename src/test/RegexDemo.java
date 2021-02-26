import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegexDemo {

    public static void main(String[] args) {
//        //匹配邮箱
//        String regex="^.+@.+(\\..+){1,}$";
//        String str="fushb@163.com";
////      System.out.println(str.matches(regex));
//
//        //匹配固定电话  4位区号-7位号码 或者 3位区号-8位号码
//        String regex2="^\\d{4}-\\d{7}|\\d{3}-\\d{8}$";
//        String str2="020-13222113";
//        String str3="0532-9989211";
////      System.out.println(str2.matches(regex2));
////      System.out.println(str3.matches(regex2));
//
//        //匹配除了a和9之外的数字或字符
//        String regex3="^[^9a]{1,}$";
//        String str4="1234fsfse";
//        String str5="a2343";
////      System.out.println(str4.matches(regex3));
////      System.out.println(str5.matches(regex3));
//
//        //^和$的用法
//        String a = "3131sasfasd".replaceAll("\\d{2}", "Z");
//        String b = "3131sasfasd".replaceAll("^\\d{2}", "Z");//仅替换字符串开头的两个数字
//        String c = "3131sdasfasd".replaceAll("sd", "Z");
//        String d = "3131sdsfasd".replaceAll("sd$", "Z");//仅替换字符串开头的两个数字
//        String f = "3131sdsfasd".replaceAll("^sd$", "Z");//仅替换字符串开头的两个数字
//        System.out.println(a);//ZZsasfasd
//        System.out.println(b);//Z31sasfasd
//        System.out.println(c);//3131ZasfaZ
//        System.out.println(d);//3131sdsfaZ
//        System.out.println(f);//3131sdsfasd
//        String str6 = "aa3131sasfasd";
//        System.out.println(str6.matches("\\d{2}")); //false
//        System.out.println(str6.matches("^\\d{2}"));//false

        String definedValue="state,type,date";
        String representFactor="id";

        String srcFunctionBody="\n" +
                "    /**\n" +
                "     * 根据ID获取同步消息记录表信息\n" +
                "     *\n" +
                "     * @param state 同步消息记录表STATE\n" +
                "     * @param type 同步消息记录表TYPE\n" +
                "     * @return 同步消息记录表POJO\n" +
                "     */\n" +
                "    @Select({\n" +
                "           \"<script>\",\n" +
                "           \" select \",\n" +
                "           \" id, data, state, type, count, \",\n" +
                "           \" reason, build_dateTime as buildDateTime, timestamp \",\n" +
                "           \" from tab_send_msg\",\n" +
                "           \" where id=#{id}\",\n" +
                "           \"</script>\"\n" +
                "    })\n" +
                "   SendMsg getSendMsgByStateAndType(@Param(\"state\") String state,@Param(\"type\") String type);\n";
//        String[] definedValues=definedValue.split("\\,");
//        DefinedFunctionResolver definedFunctionResolver=new DefaultDefinedFunctionResolver();
//        String result=definedFunctionResolver.doResolver(new DefinedFunctionDomain("state,type,date","getById","id",srcFunctionBody));
//        System.out.println(result);
//
//        JSONObject jsonObject=new JSONObject();
//        jsonObject.put("url","C:\\Users\\Administrator\\Desktop\\tool\\codebuilder\\ControllerFileTemplate.txt");
//        jsonObject.put("name","Controller模板");
//        jsonObject.put("path","controller");
//        jsonObject.put("fileNameStrategy",1);
//        jsonObject.put("isHandleFunction",1);
//
//        JSONObject jsonObject2=new JSONObject();
//        jsonObject2.put("url","C:\\Users\\Administrator\\Desktop\\tool\\codebuilder\\DoMainTemplate.txt");
//        jsonObject2.put("name","DoMain模板");
//        jsonObject2.put("path","com.fpp.code.core.domain");
//        jsonObject2.put("fileNameStrategy",1);
//        jsonObject2.put("isHandleFunction",1);
//
//        JSONArray jsonArray=new JSONArray();
//        jsonArray.add(jsonObject);
//        jsonArray.add(jsonObject2);
//        JSONObject a=new JSONObject();
//        a.put("spring模板",jsonArray);
//        System.out.println(a.toJSONString());





        Pattern ab = Pattern.compile("(?<paramPrefix>.*?)\\s+id\\s*");

        Matcher matcher=ab.matcher("@Param(\"id\") int id");
        while(matcher.find()){
            String group=matcher.group("paramPrefix");
            String[] valueS=group.split("\\s");
            String a=Stream.of(valueS).limit(valueS.length-1).collect(Collectors.joining());
            System.out.println(a);
        }
    }

}