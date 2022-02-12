package com.fpp.code.core.factory.config;

import com.fpp.code.core.template.TemplateFilePrefixNameStrategy;

import java.io.File;
import java.io.Serializable;
import java.util.Set;

/**
 * @author fpp
 */
public interface TemplateDefinition extends Cloneable, Serializable {

    /**
     * 项目地址
     * @return
     */
    String getProjectUrl();

    /**
     * 模块名
     * @return
     */
    String getModule();

    /**
     * 代码资源根路径
     * @return
     */
    String getSourcesRoot();

    /**
     * 获取源代码包路径
     * @return
     */
    String getSrcPackage();

    /**
     * 获取模板文件最终生成文件名后缀名
     * @return 模板文件最终生成文件名后缀名
     */
    String getTemplateFileSuffixName();

    /**
     * 文件前缀名命名策略
     * @return
     */
    TemplateFilePrefixNameStrategy getTemplateFilePrefixNameStrategy();

    /**
     * 获取模板文件
     * @return 模板文件
     */
    File getTemplateFile();

    /**
     * 是否控制方法
     * @return
     */
    @Deprecated
    boolean isHandleFunction();


    /**
     * 所依赖的模板名
     * @return
     */
    Set<String> getDependTemplates();

    /**
     * 模板实现的类名的全路径
     * @return
     */
    String getTemplateClassName();

    /**
     * 设置模板的类名
     * @param templateClassName
     */
    void setTemplateClassName(String templateClassName);
}
