package main.java.template;

import main.java.common.CommonFileUtils;
import main.java.config.CodeConfigException;
import main.java.config.ProjectFileConfig;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 模板核心配置
 *
 * @author fpp
 * @version 1.0
 * @date 2020/5/20 14:30
 */
public abstract class AbstractTemplate implements Template {

    private String templateName;

    private String path;

    private Template parentTemplate;

    private TemplateResolver templateResolver;

    private TemplateFilePrefixNameStrategy templateFileNameStrategy=new DefaultTemplateFilePrefixNameStrategy();

    @Override
    public TemplateFilePrefixNameStrategy getTemplateFileNameStrategy() {
        return templateFileNameStrategy;
    }

    @Override
    public void setTemplateFileNameStrategy(TemplateFilePrefixNameStrategy templateFileNameStrategy) {
        this.templateFileNameStrategy = templateFileNameStrategy;
    }

    @Override
    public String getTemplateName() {
        return templateName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public void setParentTemplate(Template parentTemplate) {
        this.parentTemplate = parentTemplate;
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

    public AbstractTemplate(String templeFileName, ProjectFileConfig projectFileConfig) throws CodeConfigException {
        this(templeFileName, new DefaultTemplateResolver(projectFileConfig));
    }

    public AbstractTemplate(String templeFileName, TemplateResolver templateResolver) {
        this.setTemplateName(templeFileName);
        this.templateResolver = templateResolver;
    }

    /**
     * 读取模板文件
     *
     * @param fileName 文件名
     * @return 模板文件的内容
     * @throws IOException
     */
    public String readTempleteFile(String fileName) throws IOException {
        InputStream inputStream = CommonFileUtils.getConfigFileInput(fileName);
        String result = IOUtils.toString(inputStream, "UTF-8");
        inputStream.close();
        return result;
    }

    /**
     * 获取父模板
     * 比如 service 为 service的实现类的父模板
     *
     * @return
     */
    @Override
    public Template getParentTemplate() {
        return parentTemplate;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    public abstract void refresh(String templeFileName) throws IOException;
}
