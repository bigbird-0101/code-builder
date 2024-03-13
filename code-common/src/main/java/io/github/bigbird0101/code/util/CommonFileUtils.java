package io.github.bigbird0101.code.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @author fpp
 * @version 1.0
 */
public abstract class CommonFileUtils {

    /**
     *
     * @param fileName 文件名
     * @return 返回文件路径
     */
    public static String getFilePath(String fileName) {
        try {
            ClassLoader classLoader = getDefaultClassLoader();
            Enumeration<URL> urls = classLoader != null ? classLoader.getResources(fileName)
                    : ClassLoader.getSystemResources(fileName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String path;
                if (Objects.nonNull(url)) {
                    path = url.getPath();
                } else {
                    path = fileName;
                }
                return path;
            }
        } catch (IOException ignored) {
        }
        return fileName;
    }


    /**
     *
     * @return 获取类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Exception ignored) {

        }
        if (null == classLoader) {
            classLoader = CommonFileUtils.class.getClassLoader();
            if (null == classLoader) {
                try {
                    classLoader = CommonFileUtils.class.getClassLoader().getParent();
                } catch (Exception ignored) {

                }
            }
        }
        return classLoader;
    }

    /**
     *
     * @param fileName 文件名
     * @return 获取配置文件输出流
     * @throws FileNotFoundException 文件没找到
     */
    public static FileOutputStream getConfigFileOut(String fileName) throws FileNotFoundException {
        try {
            return new FileOutputStream(URLDecoder.decode(getFilePath(fileName), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     *
     * @param fileName 文件名
     * @throws IOException io异常
     */
    public static void clearFileContent(String fileName) throws IOException {
        try (FileOutputStream fileWriter = CommonFileUtils.getConfigFileOut(fileName)) {
            assert fileWriter != null;
            fileWriter.write("".getBytes());
            fileWriter.flush();
        }
    }
}
