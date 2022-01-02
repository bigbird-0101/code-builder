package com.fpp.code;

import com.fpp.code.common.Utils;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {
    @Test
    public void test(){
        String srcFunctionBody="\n" +
                "    public Test getTestByNameAndAge(String name,String age){\r\n" +
                "         Test a=new Test;\r\n" +
                "         a.setId(id);\r\n" +
                "         dao.getTestByNameAndAge(id);\r\n" +
                "    }\r\n";
        String representFactor="id";
        Matcher matcher3= Pattern.compile("(.*?)(?<!set"+ Utils.firstUpperCase(representFactor)+")\\("+representFactor+"\\)", Pattern.CASE_INSENSITIVE).matcher(srcFunctionBody);
        while(matcher3.find()){
            String group = matcher3.group();
            String group1 = matcher3.group(1);
//            String group2 = matcher3.group(2);
//            String group3 = matcher3.group(3);
            System.out.println(group);
            System.out.println(group1);
//            System.out.println(group2);
//            System.out.println("group3 = " + group3);
            System.out.println(matcher3.groupCount());
        }
    }
}
