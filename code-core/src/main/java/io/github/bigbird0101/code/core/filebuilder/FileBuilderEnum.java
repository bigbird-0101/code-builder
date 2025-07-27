package io.github.bigbird0101.code.core.filebuilder;

import io.github.bigbird0101.code.core.filebuilder.definedfunction.DefaultDefinedFunctionResolver;

/**
 * 创建文件枚举
 * @author Administrator
 */

public enum FileBuilderEnum {
    /**
     * 创建新文件
     */
    NEW{
        @Override
        FileBuilder getFileBuilder() {
            DefaultFileBuilder defaultFileBuilder=new DefaultFileBuilder();
            FileCodeBuilderStrategy fileCodeBuilderStrategy=new NewFileCodeBuilderStrategy();
            fileCodeBuilderStrategy.setDefinedFunctionResolver(new DefaultDefinedFunctionResolver());
            defaultFileBuilder.setFileCodeBuilderStrategy(fileCodeBuilderStrategy);
            return defaultFileBuilder;
        }
    },
    /**
     * 在已有文件的头部生成
     */
    PREFIX{
        @Override
        FileBuilder getFileBuilder() {
            DefaultFileBuilder defaultFileBuilder=new DefaultFileBuilder();
            FileCodeBuilderStrategy fileCodeBuilderStrategy=new FileAppendPrefixCodeBuilderStrategy();
            fileCodeBuilderStrategy.setDefinedFunctionResolver(new DefaultDefinedFunctionResolver());
            defaultFileBuilder.setFileCodeBuilderStrategy(fileCodeBuilderStrategy);
            return defaultFileBuilder;
        }
    },
    /**
     * 在已有文件的尾部生成
     */
    SUFFIX{
        @Override
        FileBuilder getFileBuilder() {
            DefaultFileBuilder defaultFileBuilder=new DefaultFileBuilder();
            FileCodeBuilderStrategy fileCodeBuilderStrategy=new FileAppendSuffixCodeBuilderStrategy();
            fileCodeBuilderStrategy.setDefinedFunctionResolver(new DefaultDefinedFunctionResolver());
            defaultFileBuilder.setFileCodeBuilderStrategy(fileCodeBuilderStrategy);
            return defaultFileBuilder;
        }
    },
    /**
     * 覆盖已有文件的
     */
    OVERRIDE{
        @Override
        FileBuilder getFileBuilder() {
            DefaultFileBuilder defaultFileBuilder=new DefaultFileBuilder();
            FileCodeBuilderStrategy fileCodeBuilderStrategy=new OverrideFileCodeBuilderStrategy();
            fileCodeBuilderStrategy.setDefinedFunctionResolver(new DefaultDefinedFunctionResolver());
            defaultFileBuilder.setFileCodeBuilderStrategy(fileCodeBuilderStrategy);
            return defaultFileBuilder;
        }
    }
    ;

    /**
     * 获取文件构建对象
     * @return 文件构建对象
     */
    abstract FileBuilder getFileBuilder();
}
