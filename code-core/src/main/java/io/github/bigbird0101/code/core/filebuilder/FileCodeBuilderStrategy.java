package io.github.bigbird0101.code.core.filebuilder;


import io.github.bigbird0101.code.core.filebuilder.definedfunction.DefinedFunctionResolver;
import io.github.bigbird0101.code.exception.TemplateResolveException;

import java.util.Map;

/**
 * @author fpp
 * @version 1.0
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
    String doneCode(Map<String,Object> dataModel) throws TemplateResolveException;

    /**
     * 文件写入的方式
     */
    void fileWrite(String code, Map<String, Object> dataModel);
}
