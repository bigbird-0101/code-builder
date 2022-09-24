package com.fpp.code.core.filebuilder;

import com.fpp.code.core.template.Template;
import com.fpp.code.exception.TemplateResolveException;

/**
 * 文件生成器
 * @author fpp
 * @version 1.0
 * @date 2020/6/29 10:47
 */
public interface FileBuilder {


    /**
     * 获取文件命名构建器
     * @return 文件命名构建器
     */
    FileNameBuilder getFileNameBuilder();

    /**
     * 设置文件命名构建器
     * @param fileNameBuilder 文件命名构建器
     * @return
     */
    void setFileNameBuilder(FileNameBuilder fileNameBuilder);

    /**
     * 获取文件代码构建策略
     * @return
     */
    FileCodeBuilderStrategy getFileCodeBuilderStrategy();

    /**
     * 设置文件代码构建策略
     * @param fileCodeBuilderStrategy 文件代码构建策略
     * @return
     */
    void setFileCodeBuilderStrategy(FileCodeBuilderStrategy fileCodeBuilderStrategy);

    /**
     * 文件生成器
     * @param template 模板
     */
    void build(Template template) throws TemplateResolveException;
}
