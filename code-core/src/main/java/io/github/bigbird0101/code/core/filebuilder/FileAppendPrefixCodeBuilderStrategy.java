package io.github.bigbird0101.code.core.filebuilder;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.domain.TemplateFileClassInfo;
import io.github.bigbird0101.code.core.template.AbstractHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.AbstractNoHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.exception.TemplateResolveException;

import java.util.Map;
import java.util.Objects;

/**
 * 在文件的属性首部添加代码策略
 * @author fpp
 * @version 1.0
 */
public class FileAppendPrefixCodeBuilderStrategy extends AbstractFileCodeBuilderStrategy{
    /**
     * @param dataModel dataModel
     * @return 生成的代码
     */
    @Override
    public String doneCode(Map<String,Object> dataModel) throws TemplateResolveException {
        Template template = getTemplate();
        Objects.requireNonNull(template,"模板对象不允许为空!");

        if (template instanceof AbstractHandleFunctionTemplate handleFunctionTemplate) {
            handleFunctionTemplate.setResolverStrategy(this);
            String templeResult=handleFunctionTemplate.process(dataModel);

            String srcFilePath=template.getProjectUrl()+template.getModule()+template.getSourcesRoot()+template.getSrcPackage()+getFileNameBuilder().nameBuilder(template, dataModel);
            String srcResult=getSrcFileCode(srcFilePath);
            if(StrUtil.isNotBlank(srcResult)) {
                String result = srcResult.substring(0, srcResult.indexOf("{\r\n"));
                return result + templeResult + "}\r\n";
            }else{
                String suffix = handleFunctionTemplate.getTemplateFileClassInfoWhenResolved().getTemplateClassSuffix();
                String prefix = handleFunctionTemplate.getTemplateFileClassInfoWhenResolved().getTemplateClassPrefix();
                return prefix + templeResult + suffix;
            }
        }else if(template instanceof AbstractNoHandleFunctionTemplate){
            return template.process(dataModel);
        }else{
            return template.process(dataModel);
        }
    }

    /**
     * 文件写入的方式
     *
     * @param code code
     * @param dataModel dataModel
     */
    @Override
    public void fileWrite(String code, Map<String, Object> dataModel) {

    }

    /**
     * 解析策略
     *
     * @param templateFileClassInfo 模板的详情信息
     * @param dataModel dataModel
     */
    @Override
    public void resolverStrategy(TemplateFileClassInfo templateFileClassInfo, Map<String, Object> dataModel) {
        templateFileClassInfo.setTemplateClassPrefix("");
        templateFileClassInfo.setTemplateClassSuffix("");
        this.filterFunction(templateFileClassInfo, dataModel);
    }
}
