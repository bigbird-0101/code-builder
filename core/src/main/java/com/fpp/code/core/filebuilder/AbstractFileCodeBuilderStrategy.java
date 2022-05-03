package com.fpp.code.core.filebuilder;

import com.fpp.code.core.common.CollectionUtils;
import com.fpp.code.core.config.CoreConfig;
import com.fpp.code.core.domain.DefinedFunctionDomain;
import com.fpp.code.core.filebuilder.definedfunction.DefinedFunctionResolver;
import com.fpp.code.core.template.*;
import com.fpp.code.util.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/7/1 19:15
 */
public abstract class AbstractFileCodeBuilderStrategy implements FileCodeBuilderStrategy, ResolverStrategy {

    private static Logger logger= LogManager.getLogger(AbstractFileCodeBuilderStrategy.class);

    private CoreConfig coreConfig;

    private Template template;

    private FileNameBuilder fileNameBuilder;

    private DefinedFunctionResolver definedFunctionResolver;

    public DefinedFunctionResolver getDefinedFunctionResolver() {
        return definedFunctionResolver;
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
        Map<String, List<String>> templateSelectedGroup = this.getCoreConfig().getProjectTemplateInfoConfig().getTemplateSelectedGroup();
        if (this.getTemplate() instanceof AbstractHandleFunctionTemplate) {
            List<String> jsonArrayFunction = templateSelectedGroup.get(this.template.getTemplateName());
            if(CollectionUtils.isEmpty(jsonArrayFunction)){
                logger.warn("{} jsonArrayFunction is empty",this.template.getTemplateName());
                templateFileClassInfo.getFunctionS().clear();
                return;
            }
            Map<String, String> functionS = templateFileClassInfo.getFunctionS();
            Map<String, String> newFunction = new HashMap<>(10);
            functionS.forEach((k, v) -> jsonArrayFunction.forEach(data -> {
                if (k.equals(data)) {
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
                        definedFunctionDomain.setTableInfo((TableInfo) getTemplate().getTemplateVariables().get("tableInfo"));
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
            throw new FileNotFoundException("文件名不存在" + srcFilePath);
        }
        file.setWritable(true, false);
        InputStream inputStream = new FileInputStream(file);
        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        inputStream.close();
        return result;
    }

    /**
     * 获取tableName对应的文件路径
     *
     * @return
     */
    public String getFilePath() {
        return this.getTemplate().getProjectUrl()+"/"+this.getTemplate().getModule() + "/" + this.getTemplate().getSourcesRoot() + "/"+this.getTemplate().getSrcPackage()+"/" + getFileNameBuilder().nameBuilder(template);
    }

    @Override
    public void setDefinedFunctionResolver(DefinedFunctionResolver definedFunctionResolver) {
        this.definedFunctionResolver = definedFunctionResolver;
    }

    @Override
    public void resolverStrategy(TemplateFileClassInfo templateFileClassInfo) {
       filterFunction(templateFileClassInfo);
    }
}
