package com.fpp.code.core.filebuilder;

import com.fpp.code.common.DbUtil;
import com.fpp.code.core.config.CoreConfig;
import com.fpp.code.core.template.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 在文件的属性首部添加代码策略
 * @author fpp
 * @version 1.0
 * @date 2020/7/1 19:34
 */
public class FileAppendPrefixCodeBuilderStrategy extends AbstractFileCodeBuilderStrategy{
    /**
     * 文件代码生成器策略
     * @return
     */
    @Override
    public String doneCode() throws TemplateResolveException, IOException{
        Template template = getTemplate();
        Objects.requireNonNull(template,"模板对象不允许为空!");

        if(template instanceof AbstractHandleFunctionTemplate){
            AbstractHandleFunctionTemplate handleFunctionTemplate= (AbstractHandleFunctionTemplate) template;
            handleFunctionTemplate.setResolverStrategy(this);
            String templeResult=handleFunctionTemplate.getTemplateResult();

            String srcFilePath=template.getProjectUrl()+template.getModule()+template.getSourcesRoot()+template.getSrcPackage()+getFileNameBuilder().nameBuilder(template);
            String srcResult=getSrcFileCode(srcFilePath);
            String result = srcResult.substring(0, srcResult.indexOf("{\r\n"));
            return result + templeResult + "}\r\n";
        }else if(template instanceof AbstractNoHandleFunctionTemplate){
            return template.getTemplateResult();
        }else{
            return template.getTemplateResult();
        }
    }

    /**
     * 文件写入的方式
     *
     * @param code
     */
    @Override
    public void fileWrite(String code) {

    }

    /**
     * 解析策略
     *
     * @param templateFileClassInfo 模板的详情信息
     */
    @Override
    public void resolverStrategy(TemplateFileClassInfo templateFileClassInfo) {
        templateFileClassInfo.setTemplateClassPrefix("");
        templateFileClassInfo.setTemplateClassSuffix("");
        this.filterFunction(templateFileClassInfo);
    }
}
