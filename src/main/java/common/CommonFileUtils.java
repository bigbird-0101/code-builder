package main.java.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Objects;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/5/20 15:10
 */
public class CommonFileUtils {

    public static String getFilePath(String fileName){
        URL url= Thread.currentThread().getContextClassLoader().getResource(fileName);
        String path;
        if(Objects.nonNull(url)){
            path=url.getPath();
        }else{
            path=fileName;
        }
        return path;
    }
    public static FileInputStream getConfigFileInput(String fileName) throws UnsupportedEncodingException, FileNotFoundException {
        return new FileInputStream(URLDecoder.decode(getFilePath(fileName),"UTF-8"));
    }

    public static FileOutputStream getConfigFileOut(String fileName) throws UnsupportedEncodingException, FileNotFoundException {
        return new FileOutputStream(URLDecoder.decode(getFilePath(fileName),"UTF-8"));
    }
}
