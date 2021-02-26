package com.fpp.code.core.filebuilder;

import com.fpp.code.common.DbUtil;
import com.fpp.code.core.config.CoreConfig;
import com.fpp.code.core.template.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 在文件的末尾添加代码策略
 * @author fpp
 * @version 1.0
 * @date 2020/7/1 19:11
 */
public class FileAppendSuffixCodeBuilderStrategy extends AbstractFileCodeBuilderStrategy {
    private static Logger logger= LogManager.getLogger(FileAppendSuffixCodeBuilderStrategy.class);

    /**
     *
     * @return
     */
    @Override
    public String doneCode() throws TemplateResolveException, IOException {
        Objects.requireNonNull(getTemplate(),"模板对象不允许为空!");
        Template template = getTemplate();
        if(template instanceof AbstractHandleFunctionTemplate){
            AbstractHandleFunctionTemplate handleFunctionTemplate= (AbstractHandleFunctionTemplate) template;
            handleFunctionTemplate.setResolverStrategy(this);
            String templeResult=handleFunctionTemplate.getTemplateResult();

            String srcFilePath=getFilePath();
            String srcResult=getSrcFileCode(srcFilePath);
            String result = srcResult.substring(0, srcResult.lastIndexOf("}"));
            return result + templeResult + "\r}\r\n";
        }else if(template instanceof AbstractNoHandleFunctionTemplate){
            return template.getTemplateResult();
        }else{
            return template.getTemplateResult();
        }
    }

    /**
     * 文件写入的方式
     *  文件的末尾处写入
     * @param code
     */
    @Override
    public void fileWrite(String code) throws IOException {
        String filePath=getFilePath();
        File file = new File(filePath);
        logger.info("文件的路径 {} ",filePath);
        if (!file.exists()) {
            throw new IOException("文件名不存在" + filePath);
        }
        file.setWritable(true, false);
        OutputStream outputStream = new FileOutputStream(file);
        IOUtils.write(code, outputStream, "UTF-8");
        outputStream.flush();
        outputStream.close();
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
