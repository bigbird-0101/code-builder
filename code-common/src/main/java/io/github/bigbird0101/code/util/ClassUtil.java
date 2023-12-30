package io.github.bigbird0101.code.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.URLUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 获取接口的所有实现类 理论上也可以用来获取类的所有子类
 * 查询路径有限制，只局限于接口所在模块下，比如pandora-gateway,而非整个pandora（会递归搜索该文件夹下所以的实现类）
 * 路径中不可含中文，否则会异常。若要支持中文路径，需对该模块代码中url.getPath() 返回值进行urldecode.
 * @author Administrator
 */
public class ClassUtil {
    private static final Logger LOG = LogManager.getLogger(ClassUtil.class);
    private static final Map<Class<?>,ArrayList<Class<?>>> CACHE =new ConcurrentHashMap<>();

    public static ArrayList<Class<?>> getAllClassByInterface(Class<?> clazz) {
        ArrayList<Class<?>> objects = CACHE.get(clazz);
        if(!CollectionUtil.isEmpty(objects)){
            LOG.info("getAllClassByInterface cache {}",objects);
            return objects;
        }
        ArrayList<Class<?>> list = new ArrayList<>();
        // 判断是否是一个接口
        if (clazz.isInterface()) {
            try {
                ArrayList<Class<?>> allClass = getAllClass(clazz.getPackage().getName());
                for (Class<?> aClass : allClass) {
                    // isAssignableFrom:判定此 Class 对象所表示的类或接口与指定的 Class
                    // 参数所表示的类或接口是否相同，或是否是其超类或超接口
                    if (clazz.isAssignableFrom(aClass)) {
                        if (!clazz.equals(aClass) && !Modifier.isAbstract(aClass.getModifiers())) {
                            // 自身并不加进去
                            list.add(aClass);
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("出现异常",e);
                throw new RuntimeException("出现异常" + e.getMessage());
            }
        }
        LOG.debug("class list size :" + list.size());
        CACHE.put(clazz,list);
        return list;
    }


    /**
     * 从一个指定路径下查找所有的类
     *
     * @param packageName
     */
    private static ArrayList<Class<?>> getAllClass(String packageName) {

        if(LOG.isDebugEnabled()) {
            LOG.debug("packageName to search：{}", packageName);
        }
        List<String> classNameList = getClassName(packageName);
        if(LOG.isDebugEnabled()) {
            LOG.debug("getAllClass classNameList: {}", classNameList);
        }
        ArrayList<Class<?>> list = new ArrayList<>();

        for (String className : classNameList) {
            try {
                list.add(Class.forName(className));
            } catch (Throwable e) {
                LOG.warn("load class from name failed :{} {}", e, className);
            }
        }
        LOG.debug("find list size :" + list.size());
        return list;
    }

    /**
     * 获取某包下所有类
     *
     * @param packageName 包名
     * @return 类的完整名称
     */
    public static List<String> getClassName(String packageName) {

        List<String> fileNames = null;
        ClassLoader loader = cn.hutool.core.util.ClassUtil.getClassLoader();
        String packagePath = packageName.replace(".", "/");
        LOG.info("getClassName packagePath {} ",packagePath);
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            LOG.debug("file type : " + type);
            if (type.equals("file")) {
                String fileSearchPath = url.getPath();
                LOG.debug("fileSearchPath: " + fileSearchPath);
                fileSearchPath = fileSearchPath.substring(0, fileSearchPath.indexOf("/classes"));
                LOG.debug("fileSearchPath: " + fileSearchPath);
                fileNames = getClassNameByFile(fileSearchPath);
            } else if (type.equals("jar")) {
                try {
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    JarFile jarFile = jarURLConnection.getJarFile();
                    fileNames = getClassNameByJar(jarFile);
                } catch (java.io.IOException e) {
                    throw new RuntimeException("open Package URL failed：" + e.getMessage());
                }

            } else {
                throw new RuntimeException("file system not support! cannot load MsgProcessor！");
            }
        }
        return fileNames;
    }

    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath 文件路径
     * @return 类的完整名称
     */
    private static List<String> getClassNameByFile(String filePath) {
        List<String> myClassName = new ArrayList<String>();
        File file = new File(URLUtil.decode(filePath));
        File[] listFiles = file.listFiles();
        List<File> childFiles = Stream.of(Objects.requireNonNull(listFiles))
                .filter(s -> !s.getAbsolutePath().endsWith("test-classes"))
                .collect(Collectors.toList());
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                myClassName.addAll(getClassNameByFile(childFile.getPath()));
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
                    myClassName.add(childFilePath);
                }
            }
        }

        return myClassName;
    }

    /**
     * 从jar获取某包下所有类
     *
     * @return 类的完整名称
     */
    private static List<String> getClassNameByJar(JarFile jarFile) {
        List<String> myClassName = new ArrayList<String>();
        try {
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                LOG.debug("entrys jarfile:{}",entryName);
                if (entryName.endsWith(".class")) {
                    entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                    myClassName.add(entryName);
                    LOG.debug("Find Class :{}",entryName);
                }
            }
        } catch (Exception e) {
            LOG.error("发生异常:{},{}",e,e.getMessage());
            throw new RuntimeException("发生异常:" + e.getMessage());
        }
        return myClassName;
    }

    public void clearCache(){
        CACHE.clear();
    }
}