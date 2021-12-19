package com.fpp.code.core.template;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * 有依赖模板的项目模板
 */
@JSONType(serializer = HaveDependTemplateHandleFunctionTemplate.TemplateSerializer.class)
public class HaveDependTemplateHandleFunctionTemplate extends DefaultHandleFunctionTemplate {
    private static Logger logger= LogManager.getLogger(HaveDependTemplateHandleFunctionTemplate.class);

    private Set<String> dependTemplates;

    public Set<String> getDependTemplates() {
        return dependTemplates;
    }

    public void setDependTemplates(Set<String> dependTemplates) {
        this.dependTemplates = dependTemplates;
    }

    public HaveDependTemplateHandleFunctionTemplate() throws CodeConfigException, IOException {
    }

    public HaveDependTemplateHandleFunctionTemplate(String templateName, String templeFileName) throws CodeConfigException, IOException {
        super(templateName, templeFileName);
    }

    public HaveDependTemplateHandleFunctionTemplate(String templateName, String templeFileName, ResolverStrategy resolverStrategy) throws CodeConfigException, IOException {
        super(templateName, templeFileName, resolverStrategy);
    }

    public HaveDependTemplateHandleFunctionTemplate(String templateName, String templeFileName, Environment environment) throws CodeConfigException, IOException {
        super(templateName, templeFileName, environment);
    }

    public static  class TemplateSerializer implements ObjectSerializer {
        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
            HaveDependTemplateHandleFunctionTemplate abstractTemplate= (HaveDependTemplateHandleFunctionTemplate) object;
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("fileName",abstractTemplate.getTemplateFile().getName());
            jsonObject.put("name",abstractTemplate.getTemplateName());
            int typeValue = abstractTemplate.getTemplateFilePrefixNameStrategy().getTypeValue();
            jsonObject.put("filePrefixNameStrategy",typeValue);
            if(abstractTemplate.getTemplateFilePrefixNameStrategy() instanceof PatternTemplateFilePrefixNameStrategy){
                PatternTemplateFilePrefixNameStrategy patternTemplateFilePrefixNameStrategy= (PatternTemplateFilePrefixNameStrategy) abstractTemplate.getTemplateFilePrefixNameStrategy();
                jsonObject.put("filePrefixNameStrategyPattern",patternTemplateFilePrefixNameStrategy.getPattern());
            }
            jsonObject.put("fileSuffixName",abstractTemplate.getTemplateFileSuffixName());
            jsonObject.put("projectUrl",abstractTemplate.getProjectUrl());
            jsonObject.put("module",abstractTemplate.getModule());
            jsonObject.put("sourcesRoot",abstractTemplate.getSourcesRoot());
            jsonObject.put("srcPackage",abstractTemplate.getSrcPackage());
            jsonObject.put("dependTemplates",abstractTemplate.getDependTemplates());
            if(object instanceof AbstractHandleFunctionTemplate){
                jsonObject.put("isHandleFunction",1);
            }else if(object instanceof AbstractNoHandleFunctionTemplate){
                jsonObject.put("isHandleFunction",0);
            }
            if(logger.isInfoEnabled()){
                logger.info(" JSON Serializer {}",jsonObject);
            }
            serializer.write(jsonObject);
        }
    }
}
