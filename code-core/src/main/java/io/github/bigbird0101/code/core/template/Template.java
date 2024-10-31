package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.config.Resource;
import io.github.bigbird0101.code.core.template.targetfile.TargetFilePrefixNameStrategy;
import io.github.bigbird0101.code.exception.TemplateResolveException;

import java.io.Serializable;
import java.util.Map;

/**
 * @author fpp
 * @version 1.0
 */
public interface Template extends Cloneable, Serializable,TemplateMatchRule {
    String TABLE_INFO_KEY="tableInfo";
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
     * @param dataModel 模板需要用到的元数据模型
     * @return 生成的模板内容
     * @throws TemplateResolveException 模板解析异常
     */
    String process(Map<String,Object> dataModel) throws TemplateResolveException;

    /**
     * 获取模板最终生成文件的文件名命名策略
     * @return 生成文件的文件名命名策略
     */
    TargetFilePrefixNameStrategy getTargetFilePrefixNameStrategy();

    /**
     * 设置 模板最终生成文件的文件名前缀命名策略
     * @param targetFilePrefixNameStrategy 模板最终生成文件的文件名前缀命名策略
     */
    void setTargetFilePrefixNameStrategy(TargetFilePrefixNameStrategy targetFilePrefixNameStrategy);

    /**
     * 获取最终生成文件的后缀名
     * @return 文件的后缀名
     */
    String getTargetFileSuffixName();

    /**
     * 设置最终生成文件的后缀名
     * @param targetFileSuffixName 文件的后缀名
     */
    void setTargetFileSuffixName(String targetFileSuffixName);

    /**
     * 获取模板文件资源
     * @return 模板文件
     */
    Resource getTemplateResource();

    /**
     * 设置模板文件
     * @param templateResource templateResource
     */
    void setTemplateResource(Resource templateResource);

    /**
     * 刷新当前模板 重新加载模板中的内容
     */
    void refresh();

}
