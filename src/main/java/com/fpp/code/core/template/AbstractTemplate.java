package com.fpp.code.core.template;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.fpp.code.core.config.AbstractEnvironment;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
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

    private String path;

    private TemplateResolver templateResolver;

    private TemplateFilePrefixNameStrategy templateFileNameStrategy=new DefaultTemplateFilePrefixNameStrategy();

    private String templateFileSuffixName;

    @Override
    public TemplateFilePrefixNameStrategy getTemplateFileNameStrategy() {
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
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public TemplateResolver getTemplateResolver() {
        return templateResolver;
    }

    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
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
    }

    /**
     * 读取模板文件
     *
     * @return 模板文件的内容
     * @throws IOException
     */
    public String readTemplateFile(){
        if(null==templateFile){
            return "";
        }
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
    public String getPath() {
        return this.path;
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
                Objects.equals(getPath(), that.getPath()) &&
                Objects.equals(getTemplateResolver(), that.getTemplateResolver()) &&
                Objects.equals(getTemplateFileNameStrategy(), that.getTemplateFileNameStrategy()) &&
                Objects.equals(getTemplateFileSuffixName(), that.getTemplateFileSuffixName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTemplateName(), getProjectUrl(), getModule(), getSourcesRoot(), getSrcPackage(), getTemplateFile(), getPath(), getTemplateResolver(), getTemplateFileNameStrategy(), getTemplateFileSuffixName());
    }

    public static  class TemplateSerializer implements ObjectSerializer{
        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            AbstractTemplate abstractTemplate= (AbstractTemplate) object;
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("fileName",abstractTemplate.getTemplateFile().getName());
            jsonObject.put("name",abstractTemplate.getTemplateName());
            jsonObject.put("path",abstractTemplate.getPath());
            jsonObject.put("filePrefixNameStrategy",abstractTemplate.getTemplateFileNameStrategy().getTypeValue());
            jsonObject.put("fileSuffixName",abstractTemplate.getTemplateFileSuffixName());
            jsonObject.put("projectUrl",abstractTemplate.getProjectUrl());
            jsonObject.put("module",abstractTemplate.getModule());
            jsonObject.put("sourcesRoot",abstractTemplate.getSourcesRoot());
            jsonObject.put("srcPackage",abstractTemplate.getSrcPackage());
            if(object instanceof AbstractHandleFunctionTemplate){
                jsonObject.put("isHandleFunction",1);
            }else if(object instanceof AbstractNoHandleFunctionTemplate){
                jsonObject.put("isHandleFunction",0);
            }
            if(logger.isInfoEnabled()){
                logger.info(" JSON Serializer {}",jsonObject);
            }
            serializer.write(jsonObject);
        }
    }
}
