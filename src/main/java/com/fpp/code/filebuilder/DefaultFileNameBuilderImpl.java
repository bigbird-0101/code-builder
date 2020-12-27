package com.fpp.code.filebuilder;

import com.fpp.code.template.Template;
import com.fpp.code.template.TemplateFilePrefixNameStrategy;

/**
 * 默认文件名生成器
 * @author fpp
 * @version 1.0
 * @date 2020/6/30 17:42
 */
public class DefaultFileNameBuilderImpl implements FileNameBuilder {
    /**
     * 文件名生成方法
     *
     * @param tableName 表名
     * @return 最终的文件名
     */
    @Override
    public String nameBuilder(Template template,String tableName) {
        String result="";
        TemplateFilePrefixNameStrategy templateFilePrefixNameStrategy=template.getTemplateFileNameStrategy();
        if(null!=templateFilePrefixNameStrategy){
            result+=templateFilePrefixNameStrategy.prefixStrategy(template,tableName)+"."+template.getTemplateFileSuffixName();
        }
        return result;
    }
}
