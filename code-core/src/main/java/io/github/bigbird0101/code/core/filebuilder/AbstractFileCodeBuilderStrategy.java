package io.github.bigbird0101.code.core.filebuilder;

import cn.hutool.core.collection.CollectionUtil;
import io.github.bigbird0101.code.core.config.CoreConfig;
import io.github.bigbird0101.code.core.domain.DefinedFunctionDomain;
import io.github.bigbird0101.code.core.domain.TableInfo;
import io.github.bigbird0101.code.core.domain.TemplateFileClassInfo;
import io.github.bigbird0101.code.core.filebuilder.definedfunction.DefinedFunctionResolver;
import io.github.bigbird0101.code.core.template.AbstractHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.ResolverStrategy;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.util.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fpp
 * @version 1.0
 * @since 2020/7/1 19:15
 */
public abstract class AbstractFileCodeBuilderStrategy implements FileCodeBuilderStrategy, ResolverStrategy {

    private static final Logger logger= LogManager.getLogger(AbstractFileCodeBuilderStrategy.class);

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

    public void filterFunction(TemplateFileClassInfo templateFileClassInfo, Map<String, Object> dataModel) {
        Map<String, List<String>> templateSelectedGroup = this.getCoreConfig().getProjectTemplateInfoConfig().getTemplateSelectedGroup();
        if (this.getTemplate() instanceof AbstractHandleFunctionTemplate) {
            List<String> jsonArrayFunction = templateSelectedGroup.get(this.template.getTemplateName());
            if(CollectionUtil.isEmpty(jsonArrayFunction)){
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
                        definedFunctionDomain.setTableInfo((TableInfo) dataModel.get("tableInfo"));
                        definedFunctionDomain.setDataModel(dataModel);
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
     * @param srcFilePath srcFilePath
     * @return 获取源文件的代码
     */
    public String getSrcFileCode(String srcFilePath){
        String result ="";
        File file = new File(srcFilePath);
        if (!file.exists()) {
            return result;
        }
        file.setWritable(true, false);
        try(InputStream inputStream = Files.newInputStream(file.toPath())){
            result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }catch (Exception ignored){
        }
        return result;
    }

    /**
     * 获取tableName对应的文件路径
     *
     * @return tableName对应的文件路径
     */
    public String getFilePath(Map<String, Object> dataModel) {
        return this.getTemplate().getProjectUrl()+"/"+this.getTemplate().getModule() + "/" + this.getTemplate().getSourcesRoot() + "/"+this.getTemplate().getSrcPackage()+"/" + getFileNameBuilder().nameBuilder(template,dataModel);
    }

    @Override
    public void setDefinedFunctionResolver(DefinedFunctionResolver definedFunctionResolver) {
        this.definedFunctionResolver = definedFunctionResolver;
    }

    @Override
    public void resolverStrategy(TemplateFileClassInfo templateFileClassInfo, Map<String, Object> dataModel) {
       filterFunction(templateFileClassInfo,dataModel);
    }
}
