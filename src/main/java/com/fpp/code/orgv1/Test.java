package com.fpp.code.orgv1;

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/5/15 15:50
 */
public class Test {
    public static void main(String[] args) throws IOException {
        File file=new File("C:\\DevelopSoftwareWorkSpace\\ideaWorkSpace\\spring-code\\src\\main\\java\\org\\A.java");
        file.setWritable(true, false);
        InputStream inputStream=new FileInputStream(file);
        String s = IOUtils.toString(inputStream, "UTF-8");
        inputStream.close();
        String result=s.substring(0,s.lastIndexOf("}"));
        String b="    private int getDeleteFileIndex() {\n" +
                "        int a = 0;\n" +
                "        boolean isHave = false;\n" +
                "        return isHave ? a : -1;\n" +
                "    }";
        String relaResult=result+b+"}\r\n";
        OutputStream outputStream=new FileOutputStream(file);
        IOUtils.write(relaResult,outputStream,"UTF-8");
        outputStream.flush();
        outputStream.close();
        System.out.println(result);
    }
}
