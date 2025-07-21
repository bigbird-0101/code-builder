package io.github.bigbird0101.code.core.template;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import io.github.bigbird0101.code.core.cache.Cache;
import io.github.bigbird0101.code.core.cache.CachePool;
import io.github.bigbird0101.code.core.config.AbstractEnvironment;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.config.FileUrlResource;
import io.github.bigbird0101.code.core.config.Resource;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.template.targetfile.DefaultTargetFilePrefixNameStrategy;
import io.github.bigbird0101.code.core.template.targetfile.PatternTargetFilePrefixNameStrategy;
import io.github.bigbird0101.code.core.template.targetfile.TargetFilePrefixNameStrategy;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 模板核心配置
 *
 * @author fpp
 * @version 1.0
 */
public abstract class AbstractTemplate implements Template {
    private static final Logger LOGGER = LogManager.getLogger(AbstractTemplate.class);

    private static final Cache<String,String> MATCH_FILE_CONTENT_CACHE= CachePool.build(100);

    private String templateName;

    private String projectUrl;

    private String module;

    private String sourcesRoot;

    private String srcPackage;

    private Resource templateResource;

    private TemplateResolver templateResolver;

    private TargetFilePrefixNameStrategy targetFilePrefixNameStrategy =new DefaultTargetFilePrefixNameStrategy();

    private String targetFileSuffixName;

    @Override
    public TargetFilePrefixNameStrategy getTargetFilePrefixNameStrategy() {
        return targetFilePrefixNameStrategy;
    }

    @Override
    public void setTargetFilePrefixNameStrategy(TargetFilePrefixNameStrategy targetFilePrefixNameStrategy) {
        this.targetFilePrefixNameStrategy = targetFilePrefixNameStrategy;
    }

    @Override
    public String getTemplateName() {
        return templateName;
    }

    @Override
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    public TemplateResolver getTemplateResolver() {
        return templateResolver;
    }

    public AbstractTemplate() {
        this((String) null);
    }

    public AbstractTemplate(String templeFileName) throws CodeConfigException {
        this(templeFileName, new DefaultAbstractTemplateResolver());
    }

    public AbstractTemplate(String templeFileName, Environment environment) throws CodeConfigException {
        this(templeFileName, new DefaultAbstractTemplateResolver(environment));
    }

    public AbstractTemplate(String templeFileName, TemplateResolver templateResolver) {
        this.setTemplateName(templeFileName);
        this.templateResolver = templateResolver;
    }

    public AbstractTemplate(String templateName, URL templateFileUrl) {
        this(templateName);
        this.templateResource = new FileUrlResource(templateFileUrl);
    }

    public AbstractTemplate(URL templateFileUrl) {
        Assert.notNull(templateFileUrl, "templateFileUrl can not be null");
        String file = templateFileUrl.getFile();
        String templateName = FileNameUtil.getName(file);
        Assert.notNull(templateName, "templateName can not be null");
        this.templateName = templateName;
        this.templateResolver = new DefaultAbstractTemplateResolver();
        this.templateResource = new FileUrlResource(templateFileUrl);
    }

    public AbstractTemplate(File file) throws IOException {
        Assert.notNull(file, "file can not be null");
        String templateName = FileNameUtil.getName(file);
        Assert.notNull(templateName, "templateName can not be null");
        this.templateName = templateName;
        this.templateResolver = new DefaultAbstractTemplateResolver();
        try {
            this.templateResource = new FileUrlResource(file.getAbsolutePath());
        } catch (MalformedURLException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 读取模板文件
     *
     * @return 模板文件的内容
     * @throws IOException
     */
    public String readTemplateFile() throws IOException {
        if(null== templateResource){
            return "";
        }
        String result = IOUtils.toString(templateResource.getInputStream(), UTF_8);
        AbstractEnvironment.putTemplateContent(templateResource.getFile().getAbsolutePath(), result);
        return AbstractEnvironment.getTemplateContent(templateResource.getFile().getAbsolutePath());
    }

    @Override
    public Resource getTemplateResource() {
        return templateResource;
    }

    @Override
    public void setTemplateResource(Resource templateResource) {
        this.templateResource = templateResource;
    }

    @Override
    public String getTargetFileSuffixName() {
        return targetFileSuffixName;
    }

    @Override
    public void setTargetFileSuffixName(String targetFileSuffixName) {
        this.targetFileSuffixName = targetFileSuffixName;
    }

    @Override
    public String getProjectUrl() {
        return projectUrl;
    }

    @Override
    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    @Override
    public String getModule() {
        return module;
    }

    @Override
    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public String getSourcesRoot() {
        return sourcesRoot;
    }

    @Override
    public void setSourcesRoot(String sourcesRoot) {
        this.sourcesRoot = sourcesRoot;
    }

    @Override
    public String getSrcPackage() {
        return srcPackage;
    }

    @Override
    public void setSrcPackage(String srcPackage) {
        this.srcPackage = srcPackage;
    }

    public void setTemplateFileNameStrategy(TargetFilePrefixNameStrategy templateFileNameStrategy) {
        this.targetFilePrefixNameStrategy = templateFileNameStrategy;
    }

    @Override
    public boolean match(File file) {
        try {
            final Path path = file.toPath();
            String content = MATCH_FILE_CONTENT_CACHE.get(path.toString());
            if (null == content) {
                content = IoUtil.readUtf8(Files.newInputStream(path));
                MATCH_FILE_CONTENT_CACHE.put(path.toString(), content);
            }
            return doMatch(content);
        } catch (IOException e) {
            throw new CodeConfigException(e);
        }
    }

    /**
     * 真正匹配逻辑
     * @param content 文件内容
     * @return
     */
    protected abstract boolean doMatch(String content);

    @Override
    public Object clone(){
        return ObjectUtil.cloneByStream(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractTemplate that)) {
            return false;
        }
        return Objects.equals(getTemplateName(), that.getTemplateName()) &&
                Objects.equals(getProjectUrl(), that.getProjectUrl()) &&
                Objects.equals(getModule(), that.getModule()) &&
                Objects.equals(getSourcesRoot(), that.getSourcesRoot()) &&
                Objects.equals(getSrcPackage(), that.getSrcPackage()) &&
                Objects.equals(getTemplateResource(), that.getTemplateResource()) &&
                Objects.equals(getTemplateResolver(), that.getTemplateResolver()) &&
                Objects.equals(getTargetFilePrefixNameStrategy(), that.getTargetFilePrefixNameStrategy()) &&
                Objects.equals(getTargetFileSuffixName(), that.getTargetFileSuffixName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTemplateName(), getProjectUrl(), getModule(), getSourcesRoot(), getSrcPackage(), getTemplateResource(),getTemplateResolver(), getTargetFilePrefixNameStrategy(), getTargetFileSuffixName());
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public static  class TemplateSerializer implements ObjectSerializer{
        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
            AbstractTemplate abstractTemplate = (AbstractTemplate) object;
            JSONObject jsonObject = new JSONObject();
            if (null != abstractTemplate.getTemplateResource()) {
                try {
                    jsonObject.put("fileName", abstractTemplate.getTemplateResource().getFile().getName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            jsonObject.put("name", abstractTemplate.getTemplateName());
            final TargetFilePrefixNameStrategy targetFilePrefixNameStrategy = abstractTemplate.getTargetFilePrefixNameStrategy();
            if (null != targetFilePrefixNameStrategy) {
                int typeValue = targetFilePrefixNameStrategy.getTypeValue();
                JSONObject value = new JSONObject();
                value.put("value", typeValue);
                if (abstractTemplate.getTargetFilePrefixNameStrategy() instanceof PatternTargetFilePrefixNameStrategy patternTemplateFilePrefixNameStrategy) {
                    value.put("pattern", patternTemplateFilePrefixNameStrategy.getPattern());
                }
                jsonObject.put("filePrefixNameStrategy", value);
            }
            jsonObject.put("templateClassName",object.getClass().getName());
            jsonObject.put("fileSuffixName",abstractTemplate.getTargetFileSuffixName());
            jsonObject.put("projectUrl",abstractTemplate.getProjectUrl());
            jsonObject.put("module",abstractTemplate.getModule());
            jsonObject.put("sourcesRoot",abstractTemplate.getSourcesRoot());
            jsonObject.put("srcPackage",abstractTemplate.getSrcPackage());
            if(abstractTemplate instanceof HaveDependTemplate) {
                HaveDependTemplate haveDependTemplate=(HaveDependTemplate)object;
                jsonObject.put("dependTemplates",haveDependTemplate.getDependTemplates());
            }
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug(" JSON Serializer {}",jsonObject);
            }
            serializer.write(jsonObject);
        }
    }
}
