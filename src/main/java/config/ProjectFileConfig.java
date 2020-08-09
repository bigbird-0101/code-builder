package main.java.config;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProjectFileConfig extends AbstractConfig{
    private static final String CONFIG_PREFIX="code.project.file.";

    public ProjectFileConfig(String fileName) throws IOException {
        this(fileName,new Hashtable());
    }

    public ProjectFileConfig(String fileName, Map property) throws IOException {
        super.init(fileName,property);
    }

    @Override
    public String getProperty(String propertyKey) {
        return super.getProperty(CONFIG_PREFIX+propertyKey);
    }

    @Override
    public void coverProperty(String key, String value) throws IOException {
        super.coverProperty(CONFIG_PREFIX+key, value);
    }
    @Override
    public void coverProperty(Map<String,String> properties) throws IOException {
        Map<String,String> temp=new LinkedHashMap<>();
        properties.forEach((k,v)->{
            temp.put(CONFIG_PREFIX+k,v);
        });
        super.coverProperty(temp);
    }

    @Override
    public String toString() {
        return getPropertys().toString();
    }

    /**
     * 获取前缀
     * @return
     */
    public static String getConfigPrefix() {
        return CONFIG_PREFIX;
    }
}
