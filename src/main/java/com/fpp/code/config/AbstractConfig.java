package com.fpp.code.config;

import com.fpp.code.common.CommonFileUtils;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Administrator
 */
public abstract class AbstractConfig implements Config{
    private final PropertySources propertySources;

    protected AbstractConfig(PropertySources propertySources) {
        this.propertySources = propertySources;
    }

    @Override
    public String getProperty(String propertyKey) {
        return getProperty(propertyKey,String.class);
    }

    @Override
    public <T> T getProperty(String propertyKey, Class<T> targetClass) {
        return (T) propertySources.getPropertySource(propertyKey).getSource();
    }

    //    public void init(Map<String,String> property) throws IOException {
//        if(fileName==null|| "".equals(fileName.trim())){
//            throw new NullPointerException("文件名不允许为空");
//        }
//        readFile(fileName);
//        getPropertys().putAll(property);
//    }

//    public <T> void coverProperty(String key,T value) throws IOException {
//        PropertySource<T> propertySource=new PropertySource<T>(key,value);
//        StringBuilder result=new StringBuilder();
//        propertySources.forEach((k,v)->{
//            result.append(k).append("=").append(v).append("\r\n");
//        });
//        writeCoverFile(this.fileName,result.toString().getBytes("UTF-8"));
//    }
//
//    public void coverProperty(Map<String,String> properties) throws IOException {
//        readFile(fileName);
//        properties.forEach((k,v)->{
//            property.put(k,v);
//        });
//        StringBuilder result=new StringBuilder();
//        property.forEach((k,v)->{
//            result.append(k).append("=").append(v).append("\r\n");
//        });
//        System.out.println(result);
//        writeCoverFile(this.fileName,result.toString().getBytes("UTF-8"));
//    }
//
//
//    public void readFile(String fileName) throws IOException {
//        Properties pss = new OrderedProperties();
//        pss.load(new BufferedReader(new InputStreamReader(CommonFileUtils.getConfigFileInput(fileName))));
//        LinkedHashMap<String,String> properties=new LinkedHashMap<>(pss.size());
//        pss.stringPropertyNames().forEach((k)->{
//            properties.put(k,pss.getProperty(k));
//        });
//        setProperty(properties);
//    }



    /**
     * 覆盖文件 的写文件
     * @param fileName
     * @param data
     * @throws IOException
     */
    private void writeCoverFile(String fileName,byte[] data) throws IOException {
//        clearFileContent(fileName);
        IOUtils.write(new String(data),CommonFileUtils.getConfigFileOut(fileName), "UTF-8");
    }

    private void clearFileContent(String fileName) throws IOException {
        FileOutputStream fileWriter=CommonFileUtils.getConfigFileOut(fileName);
        fileWriter.write("".getBytes());
        fileWriter.flush();
        fileWriter.close();
    }

}
