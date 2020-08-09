package main.java.filebuilder;

import main.java.common.DbUtil;
import main.java.domain.CoreConfig;
import main.java.template.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    /**
     * 文件代码生成器策略
     *
     * @param coreConfig   核心配置文件
     * @param template 模板对象
     * @param tableName 表名
     * @param fileNameBuilder 文件名构建器
     * @return
     */
    @Override
    public String done(CoreConfig coreConfig,Template template, String tableName,FileNameBuilder fileNameBuilder) throws SQLException, ClassNotFoundException, TemplateResolveException {
        Objects.requireNonNull(template,"模板对象不允许为空!");
        Map<String, Object> temp = new HashMap<>(10);
        TableInfo tableInfo= DbUtil.getTableInfo(coreConfig.getDataSourceConfig(),tableName);
        tableInfo.setSavePath(coreConfig.getProjectTemplateInfoConfig().getProjectTargetPackageurl().replaceAll("\\/","."));
        temp.put("tableInfo", tableInfo);
        this.setCoreConfig(coreConfig);
        this.setTemplate(template);
        this.setFileNameBuilder(fileNameBuilder);
        if(template instanceof HandleFunctionTemplate){
            HandleFunctionTemplate handleFunctionTemplate= (HandleFunctionTemplate) template;
            handleFunctionTemplate.setResolverStrategy(this);
            return handleFunctionTemplate.getTempleResult(temp);
        }else if(template instanceof NoHandleFunctionTemplate){
            return template.getTempleResult(temp);
        }else{
            return template.getTempleResult(temp);
        }
    }

    /**
     * 文件写入的方式
     *  创建新的文件
     */
    @Override
    public void fileWrite(String code,String tableName) throws IOException {
        String filePath=getFilePath(tableName);
        File a = new File(filePath);
        if (a.exists()) {
            filePath = filePath + "_1.txt";
            a = new File(filePath);
        }
        System.out.println("最终的生成文件的路径"+filePath);
        FileUtils.forceMkdirParent(a);
        FileOutputStream fops = new FileOutputStream(a);
        fops.write(code.getBytes("utf-8"));
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
