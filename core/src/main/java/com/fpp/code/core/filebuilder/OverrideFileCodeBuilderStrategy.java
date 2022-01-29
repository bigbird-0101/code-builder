package com.fpp.code.core.filebuilder;

import com.fpp.code.core.template.AbstractHandleFunctionTemplate;
import com.fpp.code.core.template.AbstractNoHandleFunctionTemplate;
import com.fpp.code.core.template.Template;
import com.fpp.code.core.template.TemplateResolveException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class OverrideFileCodeBuilderStrategy extends AbstractFileCodeBuilderStrategy  {
    private static Logger logger= LogManager.getLogger(OverrideFileCodeBuilderStrategy.class);

    @Override
    public String doneCode() throws TemplateResolveException, IOException {
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

    @Override
    public void fileWrite(String code) throws IOException {
        String filePath=getFilePath();
        File a = new File(filePath);
        if (a.exists()) {
            FileWriter fileWriter =new FileWriter(a);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
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
}
