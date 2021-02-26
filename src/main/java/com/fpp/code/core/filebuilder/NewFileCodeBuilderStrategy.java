package com.fpp.code.core.filebuilder;

import com.fpp.code.common.DbUtil;
import com.fpp.code.core.config.CoreConfig;
import com.fpp.code.core.template.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 生成新文件的代码策略
 * @author fpp
 * @version 1.0
 * @date 2020/6/30 17:51
 */
public class NewFileCodeBuilderStrategy extends AbstractFileCodeBuilderStrategy {
    private static Logger logger= LogManager.getLogger(NewFileCodeBuilderStrategy.class);
    /**
     * 文件代码生成器策略
     *
     * @return
     */
    @Override
    public String doneCode() throws TemplateResolveException {
        Objects.requireNonNull(getTemplate(),"模板对象不允许为空!");
        Template template = getTemplate();
        if(template instanceof AbstractHandleFunctionTemplate){
            AbstractHandleFunctionTemplate handleFunctionTemplate= (AbstractHandleFunctionTemplate) template;
            handleFunctionTemplate.setResolverStrategy(this);
            return handleFunctionTemplate.getTemplateResult();
        }else if(template instanceof AbstractNoHandleFunctionTemplate){
            return template.getTemplateResult();
        }else{
            return template.getTemplateResult();
        }
    }

    /**
     * 文件写入的方式
     *  创建新的文件
     */
    @Override
    public void fileWrite(String code) throws IOException {
        String filePath=getFilePath();
        File a = new File(filePath);
        if (a.exists()) {
            filePath = filePath + "_1.txt";
            a = new File(filePath);
        }
        if(logger.isInfoEnabled()) {
            logger.info("最终的生成文件的路径 {} ", filePath);
        }
        FileUtils.forceMkdirParent(a);
        FileOutputStream fops = new FileOutputStream(a);
        fops.write(code.getBytes(StandardCharsets.UTF_8));
        fops.flush();
        fops.close();
    }

    /**
     * 解析策略
     *
     * @param templateFileClassInfo 模板的详情信息
     */
    @Override
    public void resolverStrategy(TemplateFileClassInfo templateFileClassInfo) {
        this.filterFunction(templateFileClassInfo);
    }
}