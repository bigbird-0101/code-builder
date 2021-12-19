package com.fpp.code.core.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fpp.code.common.Utils;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.factory.config.TemplateDefinition;
import com.fpp.code.core.template.PatternTemplateFilePrefixNameStrategy;
import com.fpp.code.core.template.TemplateFilePrefixNameStrategy;
import com.fpp.code.core.template.TemplateFilePrefixNameStrategyFactory;
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
    private static final String DEFAULT_TEMPALTE_FILE_SUFFIX = ".template";

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
                        throw new CodeConfigException("组合模板中的模板名为"+templateName+"不存在");
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
    protected void analysisTemplateDefinition(String templatesFilePath, JSONArray templates, Set<TemplateDefinitionHolder> templateDefinitionHolders, Map<String, TemplateDefinition> templateDefinitionMapTemp) throws CodeConfigException {
        Collection<File> files = FileUtils.listFiles(new File(templatesFilePath), new SuffixFileFilter(DEFAULT_TEMPALTE_FILE_SUFFIX), null);
        TemplateFilePrefixNameStrategyFactory templateFilePrefixNameStrategyFactory=new TemplateFilePrefixNameStrategyFactory();
        for(Object jsonObject:templates){
            JSONObject templateConfigInfo =(JSONObject) jsonObject;
            GenericTemplateDefinition genericTemplateDefinition =new GenericTemplateDefinition();
            genericTemplateDefinition.setFileSuffixName(Utils.setIfNull(templateConfigInfo.getString("fileSuffixName"),DEFAULT_FILE_SUFFIX_NAME));
            genericTemplateDefinition.setHandleFunction(Utils.setIfNull(templateConfigInfo.getBoolean("isHandleFunction"),DEFAULT_IS_HANDLE_FUNCTION));
            genericTemplateDefinition.setProjectUrl(templateConfigInfo.getString("projectUrl"));
            genericTemplateDefinition.setModule(templateConfigInfo.getString("module"));
            genericTemplateDefinition.setSourcesRoot(templateConfigInfo.getString("sourcesRoot"));
            genericTemplateDefinition.setSrcPackage(templateConfigInfo.getString("srcPackage"));
            genericTemplateDefinition.setDependTemplates(JSON.parseObject(Optional.ofNullable(templateConfigInfo.getString("dependTemplates")).orElse("[]"),new TypeReference<Set<String>>(){}));
            TemplateFilePrefixNameStrategy filePrefixNameStrategy = templateFilePrefixNameStrategyFactory.getTemplateFilePrefixNameStrategy(templateConfigInfo.getIntValue("filePrefixNameStrategy"));
            if(filePrefixNameStrategy instanceof PatternTemplateFilePrefixNameStrategy){
                ((PatternTemplateFilePrefixNameStrategy) filePrefixNameStrategy).setPattern(templateConfigInfo.getString("filePrefixNameStrategyPattern"));
            }
            genericTemplateDefinition.setFilePrefixNameStrategy(filePrefixNameStrategy);
            String templateName = templateConfigInfo.getString("name");
            if(Utils.isEmpty(templateName)){
                throw new CodeConfigException("模板名字不允许为空");
            }
            String templateFileName=templateConfigInfo.getString("fileName");
            if(Utils.isEmpty(templateFileName)){
                throw new CodeConfigException("模板文件名不允许为空");
            }
            File templateFile = files.stream().filter(file -> templateFileName.equals(file.getName())).findFirst().orElseThrow(()->new CodeConfigException("模板名为"+templateName+",配置模板文件名为"+templateFileName+",在"+templatesFilePath+"中不存在"));
            genericTemplateDefinition.setTemplateFile(templateFile);
            TemplateDefinitionHolder templateDefinitionHolder=new TemplateDefinitionHolder(genericTemplateDefinition,templateName);
            templateDefinitionMapTemp.put(templateName, genericTemplateDefinition);
            templateDefinitionHolders.add(templateDefinitionHolder);
        }
    }
}
