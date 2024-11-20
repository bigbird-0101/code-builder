package io.github.bigbird0101.code.core.filebuilder;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import io.github.bigbird0101.code.core.domain.TemplateFileClassInfo;
import io.github.bigbird0101.code.core.template.AbstractHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.AbstractNoHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;

/**
 * 在文件的末尾添加代码策略
 * @author fpp
 * @version 1.0
 * @since 2020/7/1 19:11
 */
public class FileAppendSuffixCodeBuilderStrategy extends AbstractFileCodeBuilderStrategy {
    private static final Logger logger = LogManager.getLogger(FileAppendSuffixCodeBuilderStrategy.class);

    /**
     * @param dataModel dataModel
     * @return 生成的代码
     */
    @Override
    public String doneCode(Map<String,Object> dataModel) throws TemplateResolveException {
        Objects.requireNonNull(getTemplate(),"模板对象不允许为空!");
        Template template = getTemplate();
        if(template instanceof AbstractHandleFunctionTemplate){
            AbstractHandleFunctionTemplate handleFunctionTemplate= (AbstractHandleFunctionTemplate) template;
            handleFunctionTemplate.setResolverStrategy(this);
            String templeResult=handleFunctionTemplate.process(dataModel);
            String srcFilePath=getFilePath(dataModel);
                String srcResult = getSrcFileCode(srcFilePath);
                if(StrUtil.isNotBlank(srcResult)) {
                    String suffix = handleFunctionTemplate.getTemplateFileClassInfoWhenResolved().getTemplateClassSuffix();
                    StaticLog.debug("{} suffix {},srcResult {}", Thread.currentThread().getName(), suffix, srcResult);
                    String result = srcResult.substring(0, srcResult.lastIndexOf(suffix.trim()));
                    return result + templeResult + suffix;
                }else {
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
     * 文件的末尾处写入
     *
     * @param code
     * @param dataModel
     */
    @Override
    public void fileWrite(String code, Map<String, Object> dataModel){
        try {
            String filePath = getFilePath(dataModel);
            File file = new File(filePath);
            logger.info("文件的路径 {} ", filePath);
            if (!file.exists()) {
                FileUtils.forceMkdirParent(file);
                file.createNewFile();
            }
            file.setWritable(true, false);
            try (OutputStream outputStream = new FileOutputStream(file)) {
                IOUtils.write(code, outputStream, "UTF-8");
                outputStream.flush();
            }
        }catch (Exception e){
            StaticLog.error(e);
        }
    }

    /**
     * 解析策略
     *
     * @param templateFileClassInfo 模板的详情信息
     * @param dataModel
     */
    @Override
    public void resolverStrategy(TemplateFileClassInfo templateFileClassInfo, Map<String, Object> dataModel) {
        templateFileClassInfo.setTemplateClassPrefix("");
        templateFileClassInfo.setTemplateClassSuffix("");
        this.filterFunction(templateFileClassInfo, dataModel);
    }
}
