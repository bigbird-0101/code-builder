package com.fpp.code.core.template;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.fpp.code.core.common.ObjectUtils;
import com.fpp.code.core.config.AbstractEnvironment;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.util.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 模板核心配置
 *
 * @author fpp
 * @version 1.0
 * @date 2020/5/20 14:30
 */
public abstract class AbstractTemplate implements Template {
    private static Logger logger= LogManager.getLogger(AbstractTemplate.class);

    private String templateName;

    private String projectUrl;

    private String module;

    private String sourcesRoot;

    private String srcPackage;

    private File templateFile;

    private Map<String,Object> templateVariables;

    private TemplateResolver templateResolver;

    private TemplateFilePrefixNameStrategy templateFileNameStrategy=new DefaultTemplateFilePrefixNameStrategy();

    private String templateFileSuffixName;

    @Override
    public TemplateFilePrefixNameStrategy getTemplateFilePrefixNameStrategy() {
        return templateFileNameStrategy;
    }

    @Override
    public void setTemplateFilePrefixNameStrategy(TemplateFilePrefixNameStrategy templateFileNameStrategy) {
        this.templateFileNameStrategy = templateFileNameStrategy;
    }

    @Override
    public String getTemplateName() {
        return templateName;
    }

    @Override
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public Map<String, Object> getTemplateVariables() {
        return templateVariables;
    }

    @Override
    public void setTemplateVariables(Map<String, Object> templateVariables) {
        this.templateVariables = templateVariables;
    }

    public TemplateResolver getTemplateResolver() {
        return templateResolver;
    }

    public AbstractTemplate() {
        this(null);
    }

    public AbstractTemplate(String templeFileName) throws CodeConfigException {
        this(templeFileName, new DefaultTemplateResolver());
    }

    public AbstractTemplate(String templeFileName, Environment environment) throws CodeConfigException {
        this(templeFileName, new DefaultTemplateResolver(environment));
    }

    public AbstractTemplate(String templeFileName, TemplateResolver templateResolver) {
        this.setTemplateName(templeFileName);
        this.templateResolver = templateResolver;
        this.initTemplateVariables();
    }

    /**
     * 读取模板文件
     *
     * @return 模板文件的内容
     * @throws IOException
     */
    public String readTemplateFile() throws IOException {
        if(null==templateFile){
            return "";
        }
        String result = IOUtils.toString(new FileInputStream(templateFile), StandardCharsets.UTF_8);
        AbstractEnvironment.putTemplateContent(templateFile.getAbsolutePath(), result);
        return AbstractEnvironment.getTemplateContent(templateFile.getAbsolutePath());
    }

    @Override
    public File getTemplateFile() {
        return templateFile;
    }

    @Override
    public void setTemplateFile(File templateFile) {
        this.templateFile = templateFile;
    }

    @Override
    public String getTemplateFileSuffixName() {
        return templateFileSuffixName;
    }

    @Override
    public void setTemplateFileSuffixName(String templateFileSuffixName) {
        this.templateFileSuffixName = templateFileSuffixName;
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

    public void setTemplateFileNameStrategy(TemplateFilePrefixNameStrategy templateFileNameStrategy) {
        this.templateFileNameStrategy = templateFileNameStrategy;
    }

    @Override
    public Object clone(){
        return ObjectUtils.deepClone(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractTemplate)) {
            return false;
        }
        AbstractTemplate that = (AbstractTemplate) o;
        return Objects.equals(getTemplateName(), that.getTemplateName()) &&
                Objects.equals(getProjectUrl(), that.getProjectUrl()) &&
                Objects.equals(getModule(), that.getModule()) &&
                Objects.equals(getSourcesRoot(), that.getSourcesRoot()) &&
                Objects.equals(getSrcPackage(), that.getSrcPackage()) &&
                Objects.equals(getTemplateFile(), that.getTemplateFile()) &&
                Objects.equals(getTemplateResolver(), that.getTemplateResolver()) &&
                Objects.equals(getTemplateFilePrefixNameStrategy(), that.getTemplateFilePrefixNameStrategy()) &&
                Objects.equals(getTemplateFileSuffixName(), that.getTemplateFileSuffixName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTemplateName(), getProjectUrl(), getModule(), getSourcesRoot(), getSrcPackage(), getTemplateFile(),getTemplateResolver(), getTemplateFilePrefixNameStrategy(), getTemplateFileSuffixName());
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    /**
     * 初始化模板参数并且构建一些基础的参数
     */
    protected void initTemplateVariables() {
        if(null==getTemplateVariables()||getTemplateVariables().isEmpty()){
            setTemplateVariables(new HashMap<>());
        }
        if(StrUtil.isNotBlank(getSrcPackage())) {
            getTemplateVariables().put("packageName", Utils.pathToPackage(getSrcPackage()));
        }
    }

    public static  class TemplateSerializer implements ObjectSerializer{
        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
            AbstractTemplate abstractTemplate = (AbstractTemplate) object;
            JSONObject jsonObject = new JSONObject();
            if (null != abstractTemplate.getTemplateFile()) {
                jsonObject.put("fileName", abstractTemplate.getTemplateFile().getName());
            }
            jsonObject.put("name", abstractTemplate.getTemplateName());
            final TemplateFilePrefixNameStrategy templateFilePrefixNameStrategy = abstractTemplate.getTemplateFilePrefixNameStrategy();
            if (null != templateFilePrefixNameStrategy) {
                int typeValue = templateFilePrefixNameStrategy.getTypeValue();
                JSONObject value = new JSONObject();
                value.put("value", typeValue);
                if (abstractTemplate.getTemplateFilePrefixNameStrategy() instanceof PatternTemplateFilePrefixNameStrategy) {
                    PatternTemplateFilePrefixNameStrategy patternTemplateFilePrefixNameStrategy = (PatternTemplateFilePrefixNameStrategy) abstractTemplate.getTemplateFilePrefixNameStrategy();
                    value.put("pattern", patternTemplateFilePrefixNameStrategy.getPattern());
                }
                jsonObject.put("filePrefixNameStrategy", value);
            }
            jsonObject.put("templateClassName",object.getClass().getName());
            jsonObject.put("fileSuffixName",abstractTemplate.getTemplateFileSuffixName());
            jsonObject.put("projectUrl",abstractTemplate.getProjectUrl());
            jsonObject.put("module",abstractTemplate.getModule());
            jsonObject.put("sourcesRoot",abstractTemplate.getSourcesRoot());
            jsonObject.put("srcPackage",abstractTemplate.getSrcPackage());
            if(abstractTemplate instanceof HaveDependTemplate) {
                HaveDependTemplate haveDependTemplate=(HaveDependTemplate)object;
                jsonObject.put("dependTemplates",haveDependTemplate.getDependTemplates());
            }
            if(logger.isInfoEnabled()){
                logger.info(" JSON Serializer {}",jsonObject);
            }
            serializer.write(jsonObject);
        }
    }
}
