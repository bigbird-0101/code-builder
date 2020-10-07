package main.java.filebuilder;

import main.java.domain.CoreConfig;
import main.java.filebuilder.definedfunction.DefaultDefinedFunctionResolver;
import main.java.template.Template;
import main.java.template.TemplateResolveException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 默认文件生成器
 * @author fpp
 * @version 1.0
 * @date 2020/6/30 18:21
 */
public class DefaultFileBuilder extends AbstractFileBuilder {

    public DefaultFileBuilder(FileNameBuilder fileNameBuilder, FileCodeBuilderStrategy fileCodeBuilderStrategy) {
        super(fileNameBuilder, fileCodeBuilderStrategy);
        fileCodeBuilderStrategy.setDefinedFunctionResolver(new DefaultDefinedFunctionResolver());
    }

    public DefaultFileBuilder(){
        this(new DefaultFileNameBuilderImpl(),new NewFileCodeBuilderStrategy());
    }

    /**
     * 文件生成器
     * @param coreConfig 核心配置文件
     * @param tableName 表名
     * @param template 模板
     */
    @Override
    public void build(CoreConfig coreConfig, String tableName, Template template) throws TemplateResolveException, IOException, SQLException, ClassNotFoundException {
        FileCodeBuilderStrategy fileCodeBuilderStrategy=getFileCodeBuilderStrategy();
        String code=fileCodeBuilderStrategy.done(coreConfig,template,tableName,getFileNameBuilder());
        fileCodeBuilderStrategy.fileWrite(code,tableName);
    }


}
