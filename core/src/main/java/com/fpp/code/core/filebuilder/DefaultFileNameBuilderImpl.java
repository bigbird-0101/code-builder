package com.fpp.code.core.filebuilder;

import com.fpp.code.core.domain.TableInfo;
import com.fpp.code.core.template.*;
import com.fpp.code.core.template.targetfile.TargetFilePrefixNameStrategy;

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
     * @param template 模板名
     * @return 最终的文件名
     */
    @Override
    public String nameBuilder(Template template) {
        String result="";
        TargetFilePrefixNameStrategy targetFilePrefixNameStrategy =template.getTemplateFilePrefixNameStrategy();
        if(null!= targetFilePrefixNameStrategy){
            TableInfo tableInfo = (TableInfo) template.getTemplateVariables().get("tableInfo");
            String tableName=tableInfo.getTableName();
            result+= targetFilePrefixNameStrategy.prefixStrategy(template,tableName)+"."+template.getTemplateFileSuffixName();
        }
        return result;
    }
}
