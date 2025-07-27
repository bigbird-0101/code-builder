package io.github.bigbird0101.code.core.filebuilder;

import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.exception.TemplateResolveException;

import java.util.Map;

/**
 * 文件生成器
 * @author fpp
 * @version 1.0
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
     */
    void setFileNameBuilder(FileNameBuilder fileNameBuilder);

    /**
     * 获取文件代码构建策略
     * @return 文件代码构建策略
     */
    FileCodeBuilderStrategy getFileCodeBuilderStrategy();

    /**
     * 设置文件代码构建策略
     * @param fileCodeBuilderStrategy 文件代码构建策略
     */
    void setFileCodeBuilderStrategy(FileCodeBuilderStrategy fileCodeBuilderStrategy);

    /**
     * 文件生成器
     * @param template 模板
     * @param dataModel 数据模型
     * @throws TemplateResolveException 模板解析异常
     */
    void build(Template template, Map<String,Object> dataModel) throws TemplateResolveException;
}
