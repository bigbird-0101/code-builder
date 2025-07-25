package io.github.bigbird0101.code.core.factory;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.bigbird0101.code.core.config.FileUrlResource;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static io.github.bigbird0101.code.core.config.AbstractEnvironment.getConfigContent;
import static java.util.Collections.emptySet;

/**
 * 所有模板扫描器
 * @author fpp
 */
public abstract class AbstractTemplateScanner implements TemplateScanner {
    private static final Logger LOGGER = LogManager.getLogger(AbstractTemplateScanner.class);
    private static final String DEFAULT_TEMPLATE_FILE_SUFFIX = ".template";
    @Override
    public AllTemplateDefinitionHolder scanner(String templatesFilePath, String templateConfigPath) throws CodeConfigException {
        Assert.notBlank(templatesFilePath);
        if(StrUtil.isNotBlank(templateConfigPath)) {
            JSONObject configContent = getConfigContent(templateConfigPath);
            JSONArray multipleTemplates = configContent.getJSONArray("multipleTemplates");
            JSONArray templates = configContent.getJSONArray("templates");
            return parseScannerData(multipleTemplates, templates, templatesFilePath);
        }else{
            return parseScannerData(new JSONArray(), new JSONArray(), templatesFilePath);
        }
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
            allTemplateDefinitionHolder.setMultipleTemplateDefinitionHolders(emptySet());
            allTemplateDefinitionHolder.setTemplateDefinitionHolders(emptySet());
        }
        return allTemplateDefinitionHolder;
    }

    /**
     * 解析组合模板定义
     * @param multipleTemplates multipleTemplates
     * @param multipleTemplateDefinitionHolders multipleTemplateDefinitionHolders
     * @param templateDefinitionMapTemp templateDefinitionMapTemp
     * @throws CodeConfigException CodeConfigException
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
                        LOGGER.warn("{}组合模板中的模板名为{}不存在",name,templateName);
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
     * @param templatesFilePath templatesFilePath
     * @param templates templates
     * @param templateDefinitionHolders templateDefinitionHolders
     * @param templateDefinitionMapTemp templateDefinitionMapTemp
     * @throws CodeConfigException CodeConfigException
     */
    protected void analysisTemplateDefinition(String templatesFilePath, JSONArray templates, Set<TemplateDefinitionHolder>
            templateDefinitionHolders, Map<String, TemplateDefinition> templateDefinitionMapTemp) throws CodeConfigException {
        Collection<File> files = FileUtils.listFiles(new File(URLUtil.decode(ResourceUtil.getResourceObj(templatesFilePath).getUrl()
                .getFile())), new SuffixFileFilter(DEFAULT_TEMPLATE_FILE_SUFFIX), null);
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
            try {
                File templateFile = files.stream()
                        .filter(file -> templateFileName.equals(file.getName()))
                        .findFirst()
                        .orElseThrow(() -> new CodeConfigException("templates.json 模板名为{},配置模板文件名为{},在{}中不存在",
                                templateName, templateFileName, templatesFilePath));
                try {
                    rootTemplateDefinition.setTemplateResource(new FileUrlResource(templateFile.getAbsolutePath()));
                } catch (MalformedURLException e) {
                    throw new CodeConfigException(e);
                }
                TemplateDefinitionHolder templateDefinitionHolder = new TemplateDefinitionHolder(rootTemplateDefinition, templateName);
                templateDefinitionMapTemp.put(templateName, rootTemplateDefinition);
                templateDefinitionHolders.add(templateDefinitionHolder);
            }catch (CodeConfigException e){
                LOGGER.warn(e);
            }
        }
        files.forEach(file -> {
            final String name = FileUtil.getPrefix(file.getName());
            if(!templateDefinitionMapTemp.containsKey(name)){
                final TemplateDefinition build = TemplateDefinitionBuilder.build(file);
                templateDefinitionMapTemp.put(name, build);
                TemplateDefinitionHolder templateDefinitionHolder=new TemplateDefinitionHolder(build,name);
                templateDefinitionHolders.add(templateDefinitionHolder);
            }
        });
    }
}
