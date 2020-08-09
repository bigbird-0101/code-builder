package main.java.filebuilder;

import main.java.domain.CoreConfig;
import main.java.filebuilder.definedfunction.DefinedFunctionResolver;
import main.java.template.Template;
import main.java.template.TemplateResolveException;

import java.io.IOException;
import java.sql.SQLException;

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
     * @param coreConfig 核心配置文件
     * @param template 模板对象
     * @param tableName 表名
     * @param fileNameBuilder 文件名构建器
     * @return
     */
    String done(CoreConfig coreConfig,Template template, String tableName,FileNameBuilder fileNameBuilder) throws SQLException, ClassNotFoundException, IOException, TemplateResolveException;

    /**
     * 文件写入的方式
     */
    void fileWrite(String code,String tableName) throws IOException;
}
