package com.fpp.code.template;

import java.util.Map;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/5/15 17:30
 */
public interface Template{

    /**
     * 获取模板名
     *
     * @return 模板名
     */
    String getTemplateName();

    /**
     * 获取模板生成文件的路径
     *
     * @return 生成文件的路径
     */
    String getPath();

    /**
     * 获取根据模板中的变量表最终生成的模板内容
     *
     * @param replaceKeyValue 模板中的变量表
     * @return 生成的模板内容
     * @throws TemplateResolveException 模板解析异常
     */
    String getTempleResult(Map<String, Object> replaceKeyValue) throws TemplateResolveException;

    /**
     * 获取父模板
     *  比如 service 为 service的实现类的父模板
     * @return 父模板
     */
    Template getParentTemplate();

    /**
     * 设置父模板
     * @param template 父模板
     */
    void setParentTemplate(Template template);

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
}
