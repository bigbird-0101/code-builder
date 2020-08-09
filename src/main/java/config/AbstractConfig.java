package main.java.config;

import main.java.common.CommonFileUtils;
import main.java.common.OrderedProperties;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Administrator
 */
public abstract class AbstractConfig implements Config{
    private Map<String,String> property;
    private String fileName;
    public Map<String,String> getPropertys() {
        return property;
    }

    public void setProperty(Map<String,String> property) {
        this.property = property;
    }

    @Override
    public String getProperty(String propertyKey) {
        return property.get(propertyKey);
    }
    public void init(String fileName, Map<String,String> property) throws IOException {
        this.fileName=fileName;
        if(fileName==null|| "".equals(fileName.trim())){
            throw new NullPointerException("文件名不允许为空");
        }
        readFile(fileName);
        getPropertys().putAll(property);
    }

    public void coverProperty(String key,String value) throws IOException {
        Map<String,String> propertys=getPropertys();
        propertys.put(key,value);
        StringBuilder result=new StringBuilder();
        propertys.forEach((k,v)->{
            result.append(k).append("=").append(v).append("\r\n");
        });
        writeCoverFile(this.fileName,result.toString().getBytes("UTF-8"));
    }

    public void coverProperty(Map<String,String> properties) throws IOException {
        readFile(fileName);
        properties.forEach((k,v)->{
            property.put(k,v);
        });
        StringBuilder result=new StringBuilder();
        property.forEach((k,v)->{
            result.append(k).append("=").append(v).append("\r\n");
        });
        System.out.println(result);
        writeCoverFile(this.fileName,result.toString().getBytes("UTF-8"));
    }


    public void readFile(String fileName) throws IOException {
        Properties pss = new OrderedProperties();
        pss.load(new BufferedReader(new InputStreamReader(CommonFileUtils.getConfigFileInput(fileName))));
        LinkedHashMap<String,String> properties=new LinkedHashMap<>(pss.size());
        pss.stringPropertyNames().forEach((k)->{
            properties.put(k,pss.getProperty(k));
        });
        setProperty(properties);
    }



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
