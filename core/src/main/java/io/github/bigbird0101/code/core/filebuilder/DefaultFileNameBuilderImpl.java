package io.github.bigbird0101.code.core.filebuilder;

import io.github.bigbird0101.code.core.domain.TableInfo;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.core.template.targetfile.TargetFilePrefixNameStrategy;
import io.github.bigbird0101.code.core.template.variable.resource.TemplateVariableResource;

import java.util.Optional;

import static io.github.bigbird0101.code.core.template.variable.resource.TemplateVariableResource.DEFAULT_SRC_RESOURCE_KEY;

/**
 * 默认文件名生成器
 * @author fpp
 * @version 1.0
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
            String tableName= Optional.ofNullable(tableInfo)
                    .map(TableInfo::getTableName)
                    .orElse((String) template.getTemplateVariables()
                            .getOrDefault(DEFAULT_SRC_RESOURCE_KEY,TemplateVariableResource.DEFAULT_SRC_RESOURCE_VALUE));
            result+= targetFilePrefixNameStrategy.prefixStrategy(template,tableName)+"."+template.getTargetFileSuffixName();
        }
        return result;
    }
}
