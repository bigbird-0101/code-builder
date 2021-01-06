package com.fpp.code.factory.config;

import java.io.File;

public interface TemplateDefinition {

    /**
     * 获取模板文件最终生成文件的路径
     * @return 模板文件最终生成文件的路径
     */
    String getTemplateResultBuildPath();

    /**
     * 获取模板文件最终生成文件名
     * @return 模板文件最终生成文件名
     */
    String getTemplateResultFileNameSuffix();

    /**
     * 获取模板文件
     * @return 模板文件
     */
    File getTemplateFile();

    boolean isHandleFunction();
}
