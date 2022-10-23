package io.github.bigbird0101.code.core.factory.config;

import io.github.bigbird0101.code.core.template.targetfile.TargetFilePrefixNameStrategy;

import java.io.File;
import java.io.Serializable;
import java.util.Set;

/**
 * @author fpp
 */
public interface TemplateDefinition extends Cloneable, Serializable {

    /**
     * 项目地址
     * @return 项目地址
     */
    String getProjectUrl();

    /**
     * 模块名
     * @return 模块名
     */
    String getModule();

    /**
     * 代码资源根路径
     * @return 代码资源根路径
     */
    String getSourcesRoot();

    /**
     * 获取源代码包路径
     * @return 源代码包路径
     */
    String getSrcPackage();

    /**
     * 获取模板文件最终生成文件名后缀名
     * @return 模板文件最终生成文件名后缀名
     */
    String getTargetFileSuffixName();

    /**
     * 文件前缀名命名策略
     * @return 文件前缀名命名策略
     */
    TargetFilePrefixNameStrategy getTargetFilePrefixNameStrategy();

    /**
     * 获取模板文件
     * @return 模板文件
     */
    File getTemplateFile();

    /**
     * 是否控制方法
     * @return 是否控制方法 true=是 false-否
     */
    @Deprecated
    boolean isHandleFunction();


    /**
     * 所依赖的模板名
     * @return 所依赖的模板名
     */
    Set<String> getDependTemplates();

    /**
     * 模板实现的类名的全路径
     * @return 模板实现的类名的全路径
     */
    String getTemplateClassName();

    /**
     * 设置模板的类名
     * @param templateClassName 模板className
     */
    void setTemplateClassName(String templateClassName);
}
