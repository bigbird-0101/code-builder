package com.fpp.code.core.template;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/5/15 17:30
 */
public interface Template{

    /**
     * 项目地址
     * @return
     */
    String getProjectUrl();

    /**
     * 设置项目地址
     * @param projectUrl 项目地址
     */
    void setProjectUrl(String projectUrl);

    /**
     * 模块名
     * @return
     */
    String getModule();

    /**
     * 设置模块名
     * @param module 模块名
     */
    void setModule(String module);

    /**
     * 代码资源根路径
     * @return
     */
    String getSourcesRoot();

    /**
     * 设置源代码包根路径
     * @param sourcesRoot 源代码包根路径
     */
    void setSourcesRoot(String sourcesRoot);

    /**
     * 获取源代码包路径
     * @return
     */
    String getSrcPackage();

    /**
     * 设置源代码包路径
     * @param srcPackage 源代码包路径
     */
    void setSrcPackage(String srcPackage);
    /**
     * 获取模板名
     *
     * @return 模板名
     */
    String getTemplateName();

    /**
     * 设置模板名
     * @param templateName 模板名
     */
    void setTemplateName(String templateName);

    /**
     * 获取根据模板中的变量表最终生成的模板内容
     *
     * @param replaceKeyValue 模板中的变量表
     * @return 生成的模板内容
     * @throws TemplateResolveException 模板解析异常
     */
    String getTempleResult(Map<String, Object> replaceKeyValue) throws TemplateResolveException;

    /**
     * 获取模板最终生成文件的文件名命名策略
     * @return 生成文件的文件名命名策略
     */
    TemplateFilePrefixNameStrategy getTemplateFileNameStrategy();

    /**
     * 设置 模板最终生成文件的文件名命名策略
     * @param templateFileNameStrategy 模板命名策略
     */
    void setTemplateFilePrefixNameStrategy(TemplateFilePrefixNameStrategy templateFileNameStrategy);

    /**
     * 获取最终生成文件的后缀名
     * @return 文件的后缀名
     */
    String getTemplateFileSuffixName();

    /**
     * 设置最终生成文件的后缀名
     * @param templateFileSuffixName 文件的后缀名
     */
    void setTemplateFileSuffixName(String templateFileSuffixName);

    /**
     * 获取模板文件
     * @return 模板文件
     */
    File getTemplateFile();

    /**
     * 设置模板文件
     * @param file
     */
    void setTemplateFile(File file);

    /**
     * 刷新当前模板 重新加载模板中的内容
     * @throws IOException 模板文件不存在异常
     */
    void refresh() throws IOException;
}
