package main.java.config;

import java.io.IOException;
import java.util.Hashtable;
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
}
