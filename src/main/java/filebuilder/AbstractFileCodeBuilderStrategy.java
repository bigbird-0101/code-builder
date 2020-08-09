package main.java.filebuilder;

import com.alibaba.fastjson.JSONArray;
import main.java.common.Utils;
import main.java.domain.CoreConfig;
import main.java.filebuilder.definedfunction.DefinedFunctionResolver;
import main.java.orgv2.DefinedFunctionDomain;
import main.java.orgv2.ProjectTemplateInfoConfig;
import main.java.template.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/7/1 19:15
 */
public abstract class AbstractFileCodeBuilderStrategy implements FileCodeBuilderStrategy, ResolverStrategy {

    private CoreConfig coreConfig;

    private Template template;

    private FileNameBuilder fileNameBuilder;

    private DefinedFunctionResolver definedFunctionResolver;

    private TableInfo tableInfo;

    public DefinedFunctionResolver getDefinedFunctionResolver() {
        return definedFunctionResolver;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public CoreConfig getCoreConfig() {
        return coreConfig;
    }

    public void setCoreConfig(CoreConfig coreConfig) {
        this.coreConfig = coreConfig;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public FileNameBuilder getFileNameBuilder() {
        return fileNameBuilder;
    }

    public void setFileNameBuilder(FileNameBuilder fileNameBuilder) {
        this.fileNameBuilder = fileNameBuilder;
    }

    public void filterFunction(TemplateFileClassInfo templateFileClassInfo) {
        JSONArray jsonArray = this.getCoreConfig().getProjectTemplateInfoConfig().getHandleTemplateBuild();
        if (this.getTemplate() instanceof HandleFunctionTemplate) {
            JSONArray jsonArrayFunction = Utils.getJsonArrayByKey(jsonArray, this.getTemplate().getTemplateName());
            Map<String, String> functionS = templateFileClassInfo.getFunctionS();
            Map<String, String> newFunction = new HashMap<>(10);
            functionS.forEach((k, v) -> jsonArrayFunction.forEach(data -> {
                String functionNamePrefix = (String) data;
                if (k.equals(functionNamePrefix)) {
                    newFunction.put(k, v);
                }
            }));
            if (null != this.definedFunctionResolver) {
                getCoreConfig().getProjectTemplateInfoConfig().getDefinedFunctionDomainList().forEach(item -> {
                    String templateFunctionName = item.getTemplateFunctionName();
                    newFunction.remove(templateFunctionName);
                    String srcFunctionBody = templateFileClassInfo.getFunctionS().get(templateFunctionName);
                    if(Utils.isNotEmpty(srcFunctionBody)) {
                        DefinedFunctionDomain definedFunctionDomain = (DefinedFunctionDomain) item.clone();
                        definedFunctionDomain.setTemplateFunction(srcFunctionBody);
                        definedFunctionDomain.setTableInfo(getTableInfo());
                        String functionBody = this.definedFunctionResolver.doResolver(definedFunctionDomain);
                        newFunction.put(templateFunctionName + "DEFINEDFUNCTION", functionBody);
                    }
                });
            }
            templateFileClassInfo.setFunctionS(newFunction);
        }

    }

    /**
     * 获取源文件的代码
     *
     * @param srcFilePath
     * @return
     * @throws IOException
     */
    public String getSrcFileCode(String srcFilePath) throws IOException {
        File file = new File(srcFilePath);
        if (!file.exists()) {
            throw new IOException("文件名不存在" + srcFilePath);
        }
        file.setWritable(true, false);
        InputStream inputStream = new FileInputStream(file);
        String result = IOUtils.toString(inputStream, "UTF-8");
        inputStream.close();
        return result;
    }

    /**
     * 获取tableName对应的文件路径
     *
     * @param tableName
     * @return
     */
    public String getFilePath(String tableName) {
        ProjectTemplateInfoConfig projectTemplateInfoConfig = coreConfig.getProjectTemplateInfoConfig();
        return projectTemplateInfoConfig.getProjectCompleteUrl() + "/" + projectTemplateInfoConfig.getProjectTargetPackageurl() + "/" + template.getPath() + "/" + fileNameBuilder.nameBuilder(template, tableName);
    }

    @Override
    public void setDefinedFunctionResolver(DefinedFunctionResolver definedFunctionResolver) {
        this.definedFunctionResolver = definedFunctionResolver;
    }
}
