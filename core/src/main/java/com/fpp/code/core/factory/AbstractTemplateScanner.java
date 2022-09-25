package com.fpp.code.core.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.factory.config.TemplateDefinition;
import com.fpp.code.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 所有模板扫描器
 * @author fpp
 */
public abstract class AbstractTemplateScanner implements TemplateScanner {
    private static Logger logger= LogManager.getLogger(AbstractTemplateScanner.class);

    private static final String DEFAULT_FILE_SUFFIX_NAME="java";
    private static final Boolean DEFAULT_IS_HANDLE_FUNCTION=true;
    private static final String DEFAULT_TEMPLATE_FILE_SUFFIX = ".template";

    public JSONObject getConfigContent(String templatesFilePath) throws CodeConfigException {
        String result;
        try {
            result = IOUtils.toString(new FileInputStream(templatesFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            if(logger.isErrorEnabled()){
                logger.error("template config error {} ",e.getMessage());
            }
            throw new CodeConfigException(e);
        }
        return (JSONObject) JSON.parse(result);
    }

    @Override
    public AllTemplateDefinitionHolder scanner(String templatesFilePath, String templateConfigPath) throws CodeConfigException {
        JSONObject configContent = getConfigContent(templateConfigPath);
        JSONArray multipleTemplates = configContent.getJSONArray("multipleTemplates");
        JSONArray templates = configContent.getJSONArray("templates");
        return parseScannerData(multipleTemplates,templates,templatesFilePath);
    }

    protected AllTemplateDefinitionHolder parseScannerData(JSONArray multipleTemplates, JSONArray templates,String templatesFilePath) throws CodeConfigException {
        AllTemplateDefinitionHolder allTemplateDefinitionHolder=new AllTemplateDefinitionHolder();
        if(null!=templates&&!templates.isEmpty()){
            Set<TemplateDefinitionHolder> templateDefinitionHolders=new HashSet<>();
            Set<MultipleTemplateDefinitionHolder> multipleTemplateDefinitionHolders=new HashSet<>();
            Map<String, TemplateDefinition> templateDefinitionMapTemp=new HashMap<>();
            analysisTemplateDefinition(templatesFilePath,templates,templateDefinitionHolders,templateDefinitionMapTemp);
            analysisMultipleTemplateDefinition(multipleTemplates,multipleTemplateDefinitionHolders,templateDefinitionMapTemp);
            allTemplateDefinitionHolder.setTemplateDefinitionHolders(templateDefinitionHolders);
            allTemplateDefinitionHolder.setMultipleTemplateDefinitionHolders(multipleTemplateDefinitionHolders);
        }else {
            allTemplateDefinitionHolder.setMultipleTemplateDefinitionHolders(Collections.emptySet());
            allTemplateDefinitionHolder.setTemplateDefinitionHolders(Collections.emptySet());
        }
        return allTemplateDefinitionHolder;
    }

    /**
     * 解析组合模板定义
     * @param multipleTemplates
     * @param multipleTemplateDefinitionHolders
     * @param templateDefinitionMapTemp
     * @throws CodeConfigException
     */
    protected void analysisMultipleTemplateDefinition(JSONArray multipleTemplates, Set<MultipleTemplateDefinitionHolder> multipleTemplateDefinitionHolders, Map<String, TemplateDefinition> templateDefinitionMapTemp) throws CodeConfigException {
        if(null!=multipleTemplates&&!multipleTemplates.isEmpty()){
            for(Object jsonObject:multipleTemplates){
                JSONObject multipleTemplateConfigInfo =(JSONObject) jsonObject;
                String name = multipleTemplateConfigInfo.getString("name");
                JSONArray templates = multipleTemplateConfigInfo.getJSONArray("templates");
                if(null==templates||templates.isEmpty()){
                    throw new CodeConfigException("请配置组合模板中的模板");
                }
                Set<String> templateNames=new LinkedHashSet<>();
                for(Object o:templates){
                    String templateName= (String)o;
                    TemplateDefinition templateDefinition=templateDefinitionMapTemp.get(templateName);
                    if(null==templateDefinition){
                        logger.warn("{}组合模板中的模板名为{}不存在",name,templateName);
                        continue;
                    }
                    templateNames.add(templateName);
                }
                GenericMultipleTemplateDefinition genericMultipleTemplateDefinition =new GenericMultipleTemplateDefinition();
                genericMultipleTemplateDefinition.setTemplateNames(templateNames);
                MultipleTemplateDefinitionHolder multipleTemplateDefinitionHolder=new MultipleTemplateDefinitionHolder(genericMultipleTemplateDefinition,name);
                multipleTemplateDefinitionHolders.add(multipleTemplateDefinitionHolder);
            }
        }
    }

    /**
     * 解析模板定义
     * @param templatesFilePath
     * @param templates
     * @param templateDefinitionHolders
     * @param templateDefinitionMapTemp
     * @throws CodeConfigException
     */
    protected void analysisTemplateDefinition(String templatesFilePath, JSONArray templates, Set<TemplateDefinitionHolder>
            templateDefinitionHolders, Map<String, TemplateDefinition> templateDefinitionMapTemp) throws CodeConfigException {
        Collection<File> files = FileUtils.listFiles(new File(templatesFilePath), new SuffixFileFilter(DEFAULT_TEMPLATE_FILE_SUFFIX), null);
        for(Object jsonObject:templates){
            JSONObject templateConfigInfo =(JSONObject) jsonObject;
            RootTemplateDefinition rootTemplateDefinition =JSONObject.parseObject(templateConfigInfo.toJSONString(), RootTemplateDefinition.class);
            String templateName = templateConfigInfo.getString("name");
            if(Utils.isEmpty(templateName)){
                throw new CodeConfigException("模板名字不允许为空");
            }
            String templateFileName=templateConfigInfo.getString("fileName");
            if(Utils.isEmpty(templateFileName)){
                throw new CodeConfigException("模板文件名不允许为空");
            }
            File templateFile = files.stream()
                    .filter(file -> templateFileName.equals(file.getName()))
                    .findFirst()
                    .orElseThrow(()->new CodeConfigException("templates.json 模板名为{},配置模板文件名为{},在{}中不存在",
                            templateName, templateFileName,templatesFilePath));
            rootTemplateDefinition.setTemplateFile(templateFile);
            TemplateDefinitionHolder templateDefinitionHolder=new TemplateDefinitionHolder(rootTemplateDefinition,templateName);
            templateDefinitionMapTemp.put(templateName, rootTemplateDefinition);
            templateDefinitionHolders.add(templateDefinitionHolder);
        }
    }
}
