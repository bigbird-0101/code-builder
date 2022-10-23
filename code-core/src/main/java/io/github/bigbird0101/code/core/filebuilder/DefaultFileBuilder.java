package io.github.bigbird0101.code.core.filebuilder;

import io.github.bigbird0101.code.core.filebuilder.definedfunction.DefaultDefinedFunctionResolver;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.exception.TemplateResolveException;

/**
 * 默认文件生成器
 * @author fpp
 * @version 1.0
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
    public void build(Template template) throws TemplateResolveException {
        AbstractFileCodeBuilderStrategy fileCodeBuilderStrategy= (AbstractFileCodeBuilderStrategy) getFileCodeBuilderStrategy();
        fileCodeBuilderStrategy.setTemplate(template);
        fileCodeBuilderStrategy.setFileNameBuilder(getFileNameBuilder());
        String code=fileCodeBuilderStrategy.doneCode().trim();
        fileCodeBuilderStrategy.fileWrite(code);
    }
}
