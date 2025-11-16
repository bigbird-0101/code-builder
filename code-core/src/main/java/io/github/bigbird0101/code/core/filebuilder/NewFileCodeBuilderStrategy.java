package io.github.bigbird0101.code.core.filebuilder;

import cn.hutool.log.StaticLog;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 生成新文件的代码策略
 * @author fpp
 * @version 1.0
 */
public class NewFileCodeBuilderStrategy extends AbstractFileCodeBuilderStrategy {
    private static final Logger logger= LogManager.getLogger(NewFileCodeBuilderStrategy.class);

    /**
     * 文件写入的方式
     *  创建新的文件
     */
    @Override
    public void fileWrite(String code, Map<String, Object> dataModel){
        try {
            String filePath = getFilePath(dataModel);
            File a = new File(filePath);
            if (a.exists()) {
                filePath = filePath + "_1.txt";
                a = new File(filePath);
            }
            if (logger.isInfoEnabled()) {
                logger.info("最终的生成文件的路径 {} ", filePath);
            }
            FileUtils.forceMkdirParent(a);
            try(FileOutputStream fops = new FileOutputStream(a)){
                fops.write(code.getBytes(StandardCharsets.UTF_8));
                fops.flush();
            }
        }catch (Exception e){
            StaticLog.error(e);
        }
    }
}