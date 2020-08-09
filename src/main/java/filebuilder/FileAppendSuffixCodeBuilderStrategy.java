package main.java.filebuilder;

import main.java.common.DbUtil;
import main.java.domain.CoreConfig;
import main.java.orgv2.ProjectTemplateInfoConfig;
import main.java.template.*;
import org.apache.commons.io.IOUtils;

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
    /**
     * 文件代码生成器策略
     *
     * @param coreConfig      核心配置文件
     * @param template        模板对象
     * @param tableName       表名
     * @param fileNameBuilder 文件名构建器
     * @return
     */
    @Override
    public String done(CoreConfig coreConfig, Template template, String tableName, FileNameBuilder fileNameBuilder) throws SQLException, ClassNotFoundException, IOException, TemplateResolveException {
        Objects.requireNonNull(template,"模板对象不允许为空!");
        Map<String, Object> temp = new HashMap<>(10);
        TableInfo tableInfo= DbUtil.getTableInfo(coreConfig.getDataSourceConfig(),tableName);
        ProjectTemplateInfoConfig projectTemplateInfoConfig=coreConfig.getProjectTemplateInfoConfig();
        tableInfo.setSavePath(projectTemplateInfoConfig.getProjectTargetPackageurl().replaceAll("\\/","."));
        temp.put("tableInfo", tableInfo);
        this.setCoreConfig(coreConfig);
        this.setTemplate(template);
        this.setFileNameBuilder(fileNameBuilder);
        this.setTableInfo(tableInfo);
        if(template instanceof HandleFunctionTemplate){
            HandleFunctionTemplate handleFunctionTemplate= (HandleFunctionTemplate) template;
            handleFunctionTemplate.setResolverStrategy(this);
            String templeResult=handleFunctionTemplate.getTempleResult(temp);

            String srcFilePath=getFilePath(tableName);
            String srcResult=getSrcFileCode(srcFilePath);
            String result = srcResult.substring(0, srcResult.lastIndexOf("}"));
            return result + templeResult + "\r}\r\n";
        }else if(template instanceof NoHandleFunctionTemplate){
            return template.getTempleResult(temp);
        }else{
            return template.getTempleResult(temp);
        }
    }

    /**
     * 文件写入的方式
     *  文件的末尾处写入
     * @param code
     * @param tableName
     */
    @Override
    public void fileWrite(String code, String tableName) throws IOException {
        String filePath=getFilePath(tableName);
        File file = new File(filePath);
        System.out.println("文件的路径"+filePath);
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
