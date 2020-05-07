package main.java.config;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractConfig implements Config{
    private Properties property;

    private String fileName;
    public Properties getPropertys() {
        return property;
    }

    public void setProperty(Properties property) {
        this.property = property;
    }

    @Override
    public String getProperty(String propertyKey) {
        return property.getProperty(propertyKey);
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
        Properties jsonObject=getPropertys();
        jsonObject.put(key,value);
        StringBuilder result=new StringBuilder();
        jsonObject.forEach((k,v)->{
            result.append(k).append("=").append(v).append("\r\n");
        });
        writeCoverFile(this.fileName,result.toString().getBytes("UTF-8"));
    }


    public void readFile(String fileName) throws IOException {
        InputStream insss =this.getClass().getResourceAsStream("/main/resources/"+fileName);
        Properties pss = new Properties();
        pss.load(insss);
        setProperty(pss);
    }

    /**
     * 覆盖文件 的写文件
     * @param fileName
     * @param data
     * @throws IOException
     */
    private void writeCoverFile(String fileName,byte[] data) throws IOException {
        IOUtils.write(String.valueOf(data),new PrintStream(fileName),"UTF-8");
    }

}
