package com.fpp.code.core.filebuilder;

/**
 * @author Administrator
 */
public class FileBuilderFactory {
    public static  FileBuilder getFileBuilder(FileBuilderEnum fileBuilderEnum){
       return fileBuilderEnum.getFileBuilder();
    }
}
