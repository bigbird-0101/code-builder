package com.fpp.code.core.filebuilder;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/30 18:20
 */
public abstract class AbstractFileBuilder implements FileBuilder{
    private FileNameBuilder fileNameBuilder;
    private FileCodeBuilderStrategy fileCodeBuilderStrategy;

    public AbstractFileBuilder(FileNameBuilder fileNameBuilder, FileCodeBuilderStrategy fileCodeBuilderStrategy) {
        this.fileNameBuilder = fileNameBuilder;
        this.fileCodeBuilderStrategy = fileCodeBuilderStrategy;
    }

    @Override
    public FileNameBuilder getFileNameBuilder() {
        return fileNameBuilder;
    }

    @Override
    public void setFileNameBuilder(FileNameBuilder fileNameBuilder) {
        this.fileNameBuilder = fileNameBuilder;
    }

    @Override
    public FileCodeBuilderStrategy getFileCodeBuilderStrategy() {
        return fileCodeBuilderStrategy;
    }

    @Override
    public void setFileCodeBuilderStrategy(FileCodeBuilderStrategy fileCodeBuilderStrategy) {
        this.fileCodeBuilderStrategy = fileCodeBuilderStrategy;
    }


}
