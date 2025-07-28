package io.github.bigbird0101.code.core.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.github.bigbird0101.code.core.common.OrderedProperties;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.util.CommonFileUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static cn.hutool.core.text.StrPool.SLASH;

/**
 * @author fpp
 * @version 1.0
 */
public abstract class AbstractEnvironment implements Environment {
    private static final Logger logger = LogManager.getLogger(AbstractEnvironment.class);
    public static final String DEFAULT_TEMPLATE_FILE_SUFFIX = ".template";
    public static final String DEFAULT_USER_SAVE_TEMPLATE_CONFIG = "code.user.save.config";
    public static final String DEFAULT_CORE_TEMPLATE_PATH = "code.template.config";
    public static final String DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE = "code.template.config.template";
    public static final String DEFAULT_CORE_TEMPLATE_FILES_PATH = "code.template.files";
    /**
     * 容器初始化时，是否需要刷新模板
     */
    public static final String CODE_CONTEXT_TEMPLATE_INIT_REFRESH="code.context.template.init.refresh";
    public static final String MULTIPLE_TEMPLATES = "multipleTemplates";
    public static final String TEMPLATES = "templates";
    private final transient MultiplePropertySources DEFAULT_MULTIPLE = new MultiplePropertySources();
    private final transient PropertySourcesPropertyResolver propertySourcesPropertyResolver = new PropertySourcesPropertyResolver(DEFAULT_MULTIPLE);
    private static final Map<String, String> TEMPLATE_FILE_URL_CONTENT_MAPPING = new HashMap<>();
    private static final Map<String, String> TEMPLATE_FILE_NAME_URL_MAPPING = new HashMap<>();
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
    public static String getTemplateContent(String templateFileUrl) {
        return TEMPLATE_FILE_URL_CONTENT_MAPPING.get(templateFileUrl);
    }

    public static String putTemplateContent(String templateFileUrl,String content) {
        return TEMPLATE_FILE_URL_CONTENT_MAPPING.put(templateFileUrl,content);
    }

    public static String getTemplateFileUrl(String templateFileName) {
        return TEMPLATE_FILE_NAME_URL_MAPPING.get(templateFileName);
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
            PropertySources propertySources = getPropertySources();
            propertySources.addPropertySource(new StringPropertySource(DEFAULT_CORE_TEMPLATE_PATH,
                    getDecodeFilePath(getTemplateConfigPath())));
            propertySources.addPropertySource(new StringPropertySource(DEFAULT_CORE_TEMPLATE_FILES_PATH,
                    getDecodeFilePath(getTemplatesPath())));
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
        Collection<File> files = FileUtils.listFiles(getTemplateDirectory(), new SuffixFileFilter(DEFAULT_TEMPLATE_FILE_SUFFIX), null);
        files.forEach(file -> {
            String fileName = file.getName();
            TEMPLATE_FILE_NAME_URL_MAPPING.put(fileName, file.getAbsolutePath());
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                TEMPLATE_FILE_URL_CONTENT_MAPPING.put(file.getAbsolutePath(), IOUtils.toString(fileInputStream, StandardCharsets.UTF_8));
            } catch (IOException e) {
                InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatesPath);
                try {
                    assert resourceAsStream != null;
                    TEMPLATE_FILE_URL_CONTENT_MAPPING.put(file.getAbsolutePath(), IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8));
                } catch (IOException ignored) {
                }
            }
        });
    }

    public File getTemplateDirectory() {
        return new File(getDecodeFilePath(templatesPath));
    }

    private String getDecodeFilePath(String path) {
        Resource resourceObj = ResourceUtil.getResourceObj(path);
        String decodePath = URLUtil.decode(resourceObj.getUrl().getFile());
        if (StrUtil.startWith(decodePath, SLASH) && FileUtil.isWindows()) {
            decodePath = StrUtil.removePrefix(decodePath, SLASH);
        }
        return decodePath;
    }

    public void loadCoreConfig(String fileName) throws IOException {
        Properties pss = new OrderedProperties();
        logger.info("加载配置环境文件名 {}",fileName);
        try (Reader reader = ResourceUtil.getUtf8Reader(fileName)) {
            pss.load(reader);
        }
        if(logger.isInfoEnabled()){
            logger.info("加载配置环境 {}",pss);
        }
        pss.stringPropertyNames().forEach((k) -> {
            StringPropertySource multipleTemplatePropertySource = new StringPropertySource(k, pss.getProperty(k));
            getPropertySources().addPropertySource(multipleTemplatePropertySource);
        });
    }


    @Override
    public void refresh() throws CodeConfigException {
        parse();
    }

    @SafeVarargs
    @Override
    public final <T> void refreshPropertySourceSerialize(PropertySource<T>... propertySources) {
        boolean isTemplateCorePath = false;
        if (propertySources.length > 0) {
            isTemplateCorePath = propertySources[0].getName().equals(DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE);
        }
        if (isTemplateCorePath) {
            JSONObject configContent = getConfigContent(templateConfigPath);
            JSONArray multipleTemplates = configContent.getJSONArray(MULTIPLE_TEMPLATES);
            JSONArray templates = configContent.getJSONArray(TEMPLATES);
            Arrays.stream(propertySources).forEach(propertySource -> {
                if (propertySource.getSource() instanceof Template template) {
                    int index = isHaveTemplate(templates, template.getTemplateName());
                    if(index>=0) {
                        templates.remove(index);
                    }
                    templates.add(JSON.toJSON(template));
                } else if (propertySource.getSource() instanceof MultipleTemplate template) {
                    int index = isHaveTemplate(multipleTemplates, template.getTemplateName());
                    if(index>=0) {
                        multipleTemplates.remove(index);
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", template.getTemplateName());
                    jsonObject.put(TEMPLATES, template.getTemplates().stream().map(Template::getTemplateName).toArray());
                    multipleTemplates.add(jsonObject);
                }
            });
            doSetTemplateConfig(configContent);
        } else {
            Arrays.stream(propertySources).forEach(propertySource -> getPropertySources().updatePropertySource(propertySource.getName(), propertySource.getSource()));
            doSetCoreConfig();
        }
    }

    private void doSetCoreConfig() {
        if(StrUtil.isNotBlank(coreConfigPath)) {
            Properties properties = getPropertySources().convertProperties();
            StringBuilder stringBuilder = new StringBuilder();
            properties.forEach((k, v) -> {
                if (!k.equals(DEFAULT_CORE_TEMPLATE_PATH) && !k.equals(DEFAULT_CORE_TEMPLATE_FILES_PATH)) {
                    stringBuilder.append(k).append("=").append(v).append("\r\n");
                }
            });
            try (FileOutputStream fileOutputStream = new FileOutputStream(coreConfigPath)) {
                CommonFileUtils.clearFileContent(coreConfigPath);
                IOUtils.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8), fileOutputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void doSetTemplateConfig(JSONObject configContent) {
        if(StrUtil.isNotBlank(templateConfigPath)) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(templateConfigPath)) {
                CommonFileUtils.clearFileContent(templateConfigPath);
                String result = JSON.toJSONString(configContent, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                        SerializerFeature.WriteDateUseDateFormat);
                IOUtils.write(result.getBytes(StandardCharsets.UTF_8), fileOutputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SafeVarargs
    @Override
    public final <T> void removePropertySourceSerialize(PropertySource<T>... propertySources) {
        boolean isTemplateCorePath = false;
        if (propertySources.length > 0) {
            isTemplateCorePath = propertySources[0].getName().equals(DEFAULT_CORE_TEMPLATE_PATH_TEMPLATE);
        }
        if (isTemplateCorePath) {
            JSONObject configContent = getConfigContent(templateConfigPath);
            JSONArray multipleTemplates = configContent.getJSONArray(MULTIPLE_TEMPLATES);
            JSONArray templates = configContent.getJSONArray(TEMPLATES);
            Arrays.stream(propertySources).forEach(propertySource -> {
                if (propertySource.getSource() instanceof Template template) {
                    int index = isHaveTemplate(templates, template.getTemplateName());
                    if (index >= 0) {
                        templates.remove(index);
                    }
                } else if (propertySource.getSource() instanceof MultipleTemplate template) {
                    int index = isHaveTemplate(multipleTemplates, template.getTemplateName());
                    if (index >= 0) {
                        multipleTemplates.remove(index);
                    }
                }
            });
            doSetTemplateConfig(configContent);
        } else {
            Arrays.stream(propertySources).forEach(propertySource -> getPropertySources().removeIfPresent(propertySource));
            doSetCoreConfig();
        }
    }

    private int isHaveTemplate(JSONArray jsonArray, String templateName) {
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

    public static JSONObject getConfigContent(String templatesFilePath) throws CodeConfigException {
        String result;
        try {
            result = IOUtils.toString(Files.newInputStream(Paths.get(templatesFilePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CodeConfigException(e);
        }
        JSONObject parse = (JSONObject) JSON.parse(result);
        if (CollUtil.isEmpty(parse)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MULTIPLE_TEMPLATES, new JSONArray());
            jsonObject.put(TEMPLATES, new JSONArray());
            return jsonObject;
        }
        return parse;
    }

    /**
     * 设置容器初始化时是否需要刷新
     * @param initRefresh
     */
    public void setContextTemplateInitRefresh(Boolean initRefresh){
        getPropertySources().addPropertySource(new GenericPropertySource<>(CODE_CONTEXT_TEMPLATE_INIT_REFRESH,initRefresh));
    }

    @Override
    public String toString() {
        return "AbstractEnvironment{" +
                "property source=" + DEFAULT_MULTIPLE +
                '}';
    }
}