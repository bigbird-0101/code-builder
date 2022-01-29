package com.fpp.code.core.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fpp.code.core.common.OrderedProperties;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.template.MultipleTemplate;
import com.fpp.code.core.template.Template;
import com.fpp.code.util.CommonFileUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/12/22 11:16
 */
public abstract class AbstractEnvironment implements Environment {
    private static Logger logger = LogManager.getLogger(AbstractEnvironment.class);
    private static final String DEFAULT_TEMPALTE_FILE_SUFFIX = ".template";
    public static final String DEFAULT_CORE_TEMPLATE_PATH = "code.template.config";
    public static final String DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE = "code.template.config.template";
    public static final String DEFAULT_CORE_TEMPLATE_FILES_PATH = "code.template.files";
    private final MultiplePropertySources DEFAULT_MULTIPLE = new MultiplePropertySources();
    private final PropertySourcesPropertyResolver propertySourcesPropertyResolver = new PropertySourcesPropertyResolver(DEFAULT_MULTIPLE);
    private static Map<String, String> tepmlateFileUrlContentMapping = new HashMap<>();
    private static Map<String, String> tepmlateFileNameUrlMapping = new HashMap<>();
    private String coreConfigPath;
    private String templateConfigPath;
    private String templatesPath;

    @Override
    public String getProperty(String propertyKey) {
        return propertySourcesPropertyResolver.getProperty(propertyKey);
    }

    @Override
    public <T> T getProperty(String propertyKey, Class<T> targetClass) {
        return propertySourcesPropertyResolver.getProperty(propertyKey, targetClass);
    }
    public static String getTemplateContent(String teplateFileUrl) {
        return tepmlateFileUrlContentMapping.get(teplateFileUrl);
    }

    public static String putTemplateContent(String teplateFileUrl,String content) {
        return tepmlateFileUrlContentMapping.put(teplateFileUrl,content);
    }

    public static String getTemplateFileUrl(String teplateFileName) {
        return tepmlateFileNameUrlMapping.get(teplateFileName);
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
    public PropertySources getPropertySources() {
        return propertySourcesPropertyResolver.getPropertySources();
    }

    @Override
    public void parse() throws CodeConfigException {
        try {
            loadCoreConfig();
            getPropertySources().addPropertySource(new StringPropertySource(DEFAULT_CORE_TEMPLATE_PATH, getTemplateConfigPath()));
            getPropertySources().addPropertySource(new StringPropertySource(DEFAULT_CORE_TEMPLATE_FILES_PATH, getTemplatesPath()));
            loadTemplatesPath();
            logger.info("config parse down");
        } catch (Throwable e) {
            logger.error("parse error", e);
            throw new CodeConfigException("config file error ", e);
        }
    }

    /**
     * 加载核心配置
     */
    protected void loadCoreConfig() throws IOException {
        loadCoreConfig(coreConfigPath);
    }
    protected void loadTemplatesPath() {
        Collection<File> files = FileUtils.listFiles(new File(templatesPath), new SuffixFileFilter(DEFAULT_TEMPALTE_FILE_SUFFIX), null);
        files.forEach(file -> {
            String fileName = file.getName();
            tepmlateFileNameUrlMapping.put(fileName, file.getAbsolutePath());
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                tepmlateFileUrlContentMapping.put(file.getAbsolutePath(), IOUtils.toString(fileInputStream, StandardCharsets.UTF_8));
            } catch (IOException e) {
                InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatesPath);
                try {
                    tepmlateFileUrlContentMapping.put(file.getAbsolutePath(), IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8));
                } catch (IOException ignored) {
                }
            }
        });
    }

    public void loadCoreConfig(String fileName) throws IOException {
        Properties pss = new OrderedProperties();
        logger.info("加载配置环境文件名 {}",fileName);
        Reader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(CommonFileUtils.getConfigFileInput(fileName)), StandardCharsets.UTF_8));
        try {
            pss.load(reader);
        } finally {
            reader.close();
        }
        if(logger.isInfoEnabled()){
            logger.info("加载配置环境 {}",pss);
        }
        LinkedHashMap<String, String> properties = new LinkedHashMap<>(pss.size());
        pss.stringPropertyNames().forEach((k) -> {
            properties.put(k, pss.getProperty(k));
            StringPropertySource multipleTemplatePropertySource = new StringPropertySource(k, pss.getProperty(k));
            getPropertySources().addPropertySource(multipleTemplatePropertySource);
        });
    }


    @Override
    public void refresh() throws CodeConfigException {
        parse();
    }

    @Override
    public <T> void refreshPropertySourceSerialize(PropertySource<T>... propertySources) {
        boolean isTemplateCorePath = false;
        if (propertySources.length > 0) {
            isTemplateCorePath = propertySources[0].getName().equals(DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE);
        }
        if (isTemplateCorePath) {
            JSONObject configContent = null;
            try {
                configContent = getConfigContent(templateConfigPath);
            } catch (CodeConfigException e) {
                e.printStackTrace();
            }
            assert configContent != null;
            JSONArray multipleTemplates = configContent.getJSONArray("multipleTemplates");
            JSONArray templates = configContent.getJSONArray("templates");
            Arrays.stream(propertySources).forEach(propertySource -> {
                if (propertySource.getSource() instanceof Template) {
                    Template template = (Template) propertySource.getSource();
                    int index = IsHaveTemplate(templates, template.getTemplateName());
                    if(index>=0) {
                        templates.remove(index);
                    }
                    templates.add(JSON.toJSON(template));
                } else if (propertySource.getSource() instanceof MultipleTemplate) {
                    MultipleTemplate template = (MultipleTemplate) propertySource.getSource();
                    int index = IsHaveTemplate(multipleTemplates, template.getTemplateName());
                    if(index>=0) {
                        multipleTemplates.remove(index);
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", template.getTemplateName());
                    jsonObject.put("templates", template.getTemplates().stream().map(Template::getTemplateName).toArray());
                    multipleTemplates.add(jsonObject);
                }
            });
            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(templateConfigPath))) {
                CommonFileUtils.clearFileContent(templateConfigPath);
                String result = JSON.toJSONString(configContent, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                        SerializerFeature.WriteDateUseDateFormat);
                IOUtils.write(result.getBytes(StandardCharsets.UTF_8), fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Arrays.stream(propertySources).forEach(propertySource -> {
                getPropertySources().updatePropertySource(propertySource.getName(), propertySource.getSource());
            });
            Properties properties = getPropertySources().convertProperties(getPropertySources());
            StringBuilder stringBuilder = new StringBuilder();
            properties.forEach((k, v) -> {
                if (!k.equals(DEFAULT_CORE_TEMPLATE_PATH) && !k.equals(DEFAULT_CORE_TEMPLATE_FILES_PATH)) {
                    stringBuilder.append(k).append("=").append(v).append("\r\n");
                }
            });
            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(coreConfigPath))) {
                CommonFileUtils.clearFileContent(coreConfigPath);
                IOUtils.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8), fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public <T> void removePropertySourceSerialize(PropertySource<T>... propertySources) {
        boolean isTemplateCorePath = false;
        if (propertySources.length > 0) {
            isTemplateCorePath = propertySources[0].getName().equals(DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE);
        }
        if (isTemplateCorePath) {
            JSONObject configContent = null;
            try {
                configContent = getConfigContent(templateConfigPath);
            } catch (CodeConfigException e) {
                e.printStackTrace();
            }
            assert configContent != null;
            JSONArray multipleTemplates = configContent.getJSONArray("multipleTemplates");
            JSONArray templates = configContent.getJSONArray("templates");
            Arrays.stream(propertySources).forEach(propertySource -> {
                if (propertySource.getSource() instanceof Template) {
                    Template template = (Template) propertySource.getSource();
                    int index = IsHaveTemplate(templates, template.getTemplateName());
                    if(index>0) {
                        templates.remove(index);
                    }
                } else if (propertySource.getSource() instanceof MultipleTemplate) {
                    MultipleTemplate template = (MultipleTemplate) propertySource.getSource();
                    int index = IsHaveTemplate(multipleTemplates, template.getTemplateName());
                    if(index>0) {
                        multipleTemplates.remove(index);
                    }
                }
            });
            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(templateConfigPath))) {
                CommonFileUtils.clearFileContent(templateConfigPath);
                String result = JSON.toJSONString(configContent, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                        SerializerFeature.WriteDateUseDateFormat);
                IOUtils.write(result.getBytes(StandardCharsets.UTF_8), fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Arrays.stream(propertySources).forEach(propertySource -> {
                getPropertySources().removeIfPresent(propertySource);
            });
            Properties properties = getPropertySources().convertProperties(getPropertySources());
            StringBuilder stringBuilder = new StringBuilder();
            properties.forEach((k, v) -> {
                if (!k.equals(DEFAULT_CORE_TEMPLATE_PATH) && !k.equals(DEFAULT_CORE_TEMPLATE_FILES_PATH)) {
                    stringBuilder.append(k).append("=").append(v).append("\r\n");
                }
            });
            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(coreConfigPath))) {
                CommonFileUtils.clearFileContent(coreConfigPath);
                IOUtils.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8), fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int IsHaveTemplate(JSONArray jsonArray, String templateName) {
        if (null != jsonArray && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject templateInfo = (JSONObject) jsonArray.get(i);
                if (templateInfo.get("name").equals(templateName)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public JSONObject getConfigContent(String templatesFilePath) throws CodeConfigException {
        String result;
        try {
            result = IOUtils.toString(new FileInputStream(templatesFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CodeConfigException(e);
        }
        return (JSONObject) JSON.parse(result);
    }

    @Override
    public String toString() {
        return "AbstractEnvironment{" +
                "property source=" + DEFAULT_MULTIPLE +
                '}';
    }
}