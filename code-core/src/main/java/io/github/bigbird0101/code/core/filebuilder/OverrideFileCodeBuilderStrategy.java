package io.github.bigbird0101.code.core.filebuilder;

import cn.hutool.log.StaticLog;
import io.github.bigbird0101.code.core.template.AbstractHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.AbstractNoHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * 覆盖已有文件
 * @author Administrator
 */
public class OverrideFileCodeBuilderStrategy extends AbstractFileCodeBuilderStrategy  {
    private static Logger logger= LogManager.getLogger(OverrideFileCodeBuilderStrategy.class);

    @Override
    public String doneCode(Map<String,Object> dataModel) throws TemplateResolveException {
        Objects.requireNonNull(getTemplate(),"模板对象不允许为空!");
        Template template = getTemplate();
        if(template instanceof AbstractHandleFunctionTemplate){
            AbstractHandleFunctionTemplate handleFunctionTemplate= (AbstractHandleFunctionTemplate) template;
            handleFunctionTemplate.setResolverStrategy(this);
            String process = handleFunctionTemplate.process(dataModel);
            return process;
        }else if(template instanceof AbstractNoHandleFunctionTemplate){
            return template.process(dataModel);
        }else{
            return template.process(dataModel);
        }
    }

    @Override
    public void fileWrite(String code, Map<String, Object> dataModel){
        try {
            String filePath = getFilePath(dataModel);
            File a = new File(filePath);
            if (a.exists()) {
                FileWriter fileWriter = new FileWriter(a);
                fileWriter.write("");
                fileWriter.flush();
                fileWriter.close();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("最终的生成文件的路径 {} ", filePath);
            }
            FileUtils.forceMkdirParent(a);
            FileOutputStream fops = new FileOutputStream(a);
            fops.write(code.getBytes(StandardCharsets.UTF_8));
            fops.flush();
            fops.close();
        }catch (Exception e){
            StaticLog.error(e);
        }
    }
}
