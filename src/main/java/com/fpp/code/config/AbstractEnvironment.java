package com.fpp.code.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fpp.code.common.Utils;
import com.fpp.code.template.DefaultMultipleTemplate;
import com.fpp.code.template.MultipleTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/12/22 11:16
 */
public abstract class AbstractEnvironment implements Environment {
    private static final String DEFAULT_TEMPALTE_FILE_SUFFIX = ".template";
    private DataSourceFileConfig dataSourceFileConfig;
    private ProjectFileConfig projectFileConfig;
    private List<MultipleTemplate> multipleTemplates;
    private static Map<String, String> tepmlateFileUrlContentMapping = new HashMap<>();
    private static Map<String, String> tepmlateFileNameUrlMapping = new HashMap<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String coreConfigPath;
    private String templateConfigPath;
    private String templatesPath;

    public AbstractEnvironment() {
    }

    public AbstractEnvironment(DataSourceFileConfig dataSourceFileConfig, ProjectFileConfig projectFileConfig, List<MultipleTemplate> multipleTemplates) {
        this.dataSourceFileConfig = dataSourceFileConfig;
        this.projectFileConfig = projectFileConfig;
        this.multipleTemplates = multipleTemplates;
    }

    @Override
    public String getProperty(String propertyKey) {
        return Utils.isNotEmpty(dataSourceFileConfig.getProperty(propertyKey)) ? dataSourceFileConfig.getProperty(propertyKey) : projectFileConfig.getProperty(propertyKey);
    }

    public static String getTemplateContent(String teplateFileUrl) {
        return tepmlateFileUrlContentMapping.get(teplateFileUrl);
    }

    public static String getTemplateFileUrl(String teplateFileName) {
        return tepmlateFileNameUrlMapping.get(teplateFileName);
    }

    @Override
    public DataSourceFileConfig getDataSourceFileConfig() {
        return dataSourceFileConfig;
    }

    public void setDataSourceFileConfig(DataSourceFileConfig dataSourceFileConfig) {
        this.dataSourceFileConfig = dataSourceFileConfig;
    }

    @Override
    public ProjectFileConfig getProjectFileConfig() {
        return projectFileConfig;
    }

    public void setProjectFileConfig(ProjectFileConfig projectFileConfig) {
        this.projectFileConfig = projectFileConfig;
    }

    @Override
    public List<MultipleTemplate> getMultipleTemplates() {
        return multipleTemplates;
    }

    public void setMultipleTemplates(List<MultipleTemplate> multipleTemplates) {
        this.multipleTemplates = multipleTemplates;
    }


    public String getCoreConfigPath() {
        return coreConfigPath;
    }

    public void setCoreConfigPath(String coreConfigPath) {
        this.coreConfigPath = coreConfigPath;
    }

    public String getTemplateConfigPath() {
        return templateConfigPath;
    }

    public void setTemplateConfigPath(String templateConfigPath) {
        this.templateConfigPath = templateConfigPath;
    }

    public String getTemplatesPath() {
        return templatesPath;
    }

    public void setTemplatesPath(String templatesPath) {
        this.templatesPath = templatesPath;
    }

    @Override
    public void parse() throws CodeConfigException {
        try {
            this.dataSourceFileConfig = new DataSourceFileConfig(coreConfigPath);
            this.projectFileConfig = new ProjectFileConfig(coreConfigPath);
            loadTemplateConfig();
            loadTemplatesPath();
            System.out.println("config parse down");
        } catch (Throwable e) {
            throw new CodeConfigException("config file error ", e);
        }
    }

    protected void loadTemplatesPath() {
        Collection<File> files = FileUtils.listFiles(new File(templatesPath), new SuffixFileFilter(DEFAULT_TEMPALTE_FILE_SUFFIX), null);
        files.forEach(file -> {
            String fileName = file.getName();
            tepmlateFileNameUrlMapping.put(fileName, file.getAbsolutePath());
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                tepmlateFileUrlContentMapping.put(file.getAbsolutePath(), IOUtils.toString(fileInputStream, StandardCharsets.UTF_8));
            } catch (IOException e) {
                InputStream resourceAsStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream(templatesPath);
                try {
                    tepmlateFileUrlContentMapping.put(file.getAbsolutePath(), IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8));
                } catch (IOException ex) {
                }
            }
        });
    }

    protected void loadTemplateConfig() throws IOException {
        try (FileInputStream fileInputStreamTemplate = new FileInputStream(templateConfigPath)) {
            loadTemplateConfigByStream(fileInputStreamTemplate);
        } catch (FileNotFoundException e) {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateConfigPath);
            loadTemplateConfigByStream((FileInputStream) resourceAsStream);
        }
    }

    public void loadTemplateConfigByStream(FileInputStream fileInputStreamTemplate) throws IOException {
        String templateConfigString = IOUtils.toString(fileInputStreamTemplate, StandardCharsets.UTF_8);
        List<MultipleTemplate> multipleTemplateList = new ArrayList<>();
        if (Utils.isNotEmpty(templateConfigString)) {
            JSONArray jsonArray = JSONArray.parseArray(templateConfigString);
            jsonArray.forEach(v -> {
                JSONObject jsonObject = (JSONObject) v;
                multipleTemplateList.add(new DefaultMultipleTemplate(jsonObject));
            });
        } else {
            multipleTemplateList.add(new DefaultMultipleTemplate());
        }
        this.multipleTemplates = multipleTemplateList;
    }


    @Override
    public void refresh() throws CodeConfigException {
//        try {
        parse();
//        executorService.execute(new LoadFileRunnable(this));
//        } catch (Throwable e) {
//            throw new CodeConfigException("config file error ", e);
//        }
    }

    private static class LoadFileRunnable implements Runnable {
        private AbstractEnvironment abstractEnvironment;

        public LoadFileRunnable(AbstractEnvironment abstractEnvironment) {
            this.abstractEnvironment = abstractEnvironment;
        }

        @Override
        public void run() {
            try {
                abstractEnvironment.parse();
            } catch (CodeConfigException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public String toString() {
        return "Environment{" +
                "dataSourceFileConfig=" + dataSourceFileConfig +
                ", projectFileConfig=" + projectFileConfig +
                ", multipleTemplates=" + multipleTemplates +
                '}';
    }
}