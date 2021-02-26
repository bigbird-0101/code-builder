package com.fpp.code.core.filebuilder;

import com.fpp.code.core.config.CoreConfig;
import com.fpp.code.core.filebuilder.definedfunction.DefaultDefinedFunctionResolver;
import com.fpp.code.core.template.Template;
import com.fpp.code.core.template.TemplateResolveException;

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
     * @param template 模板
     */
    @Override
    public void build(Template template) throws IOException, TemplateResolveException {
        AbstractFileCodeBuilderStrategy fileCodeBuilderStrategy= (AbstractFileCodeBuilderStrategy) getFileCodeBuilderStrategy();
        fileCodeBuilderStrategy.setTemplate(template);
        fileCodeBuilderStrategy.setFileNameBuilder(getFileNameBuilder());
        String code=fileCodeBuilderStrategy.doneCode();
        fileCodeBuilderStrategy.fileWrite(code);
    }
}
