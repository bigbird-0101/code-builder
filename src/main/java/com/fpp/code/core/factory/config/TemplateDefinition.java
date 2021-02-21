package com.fpp.code.core.factory.config;

import com.fpp.code.core.template.TemplateFilePrefixNameStrategy;

import java.io.File;

/**
 * @author fpp
 */
public interface TemplateDefinition {

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
    String getFileSuffixName();

    /**
     * 文件前缀名命名策略
     * @return
     */
    TemplateFilePrefixNameStrategy getFilePrefixNameStrategy();

    /**
     * 获取模板文件
     * @return 模板文件
     */
    File getTemplateFile();

    /**
     * 是否控制方法
     * @return
     */
    boolean isHandleFunction();
}
