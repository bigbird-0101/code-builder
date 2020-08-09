package main.java.template;

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
     * @return
     */
    String getTemplateName();

    /**
     * 获取模板生成文件的路径
     *
     * @return
     */
    String getPath();

    /**
     * 获取根据模板中的变量表最终生成的模板内容
     *
     * @param replaceKeyValue 模板中的变量表
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    String getTempleResult(Map<String, Object> replaceKeyValue) throws TemplateResolveException;

    /**
     * 获取父模板名称
     *  比如 service 为 service的实现类的父模板
     * @return
     */
    Template getParentTemplate();

    /**
     * 获取模板最终生成文件的文件名命名策略
     * @return
     */
    TemplateFilePrefixNameStrategy getTemplateFileNameStrategy();

    /**
     * 设置 模板最终生成文件的文件名命名策略
     * @param templateFileNameStrategy
     */
    void setTemplateFileNameStrategy(TemplateFilePrefixNameStrategy templateFileNameStrategy);
}
