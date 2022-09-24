package com.fpp.code.core.filebuilder;


import com.fpp.code.core.filebuilder.definedfunction.DefinedFunctionResolver;
import com.fpp.code.exception.TemplateResolveException;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/29 11:04
 */
public interface FileCodeBuilderStrategy {

    /**
     * 设置自定义方法解析器
     * @param definedFunctionResolver
     */
    void setDefinedFunctionResolver(DefinedFunctionResolver definedFunctionResolver);

    /**
     * 文件代码生成器策略
     * @return
     */
    String doneCode() throws TemplateResolveException;

    /**
     * 文件写入的方式
     */
    void fileWrite(String code);
}
