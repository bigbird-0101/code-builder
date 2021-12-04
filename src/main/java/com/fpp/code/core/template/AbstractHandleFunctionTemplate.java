package com.fpp.code.core.template;

import com.fpp.code.common.Utils;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.template.cache.Cache;
import com.fpp.code.core.template.cache.CacheKey;
import com.fpp.code.core.template.cache.impl.CacheLocalLruImpl;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * 需要对方法进行操作的模板
 * @author fpp
 * @version 1.0
 * @date 2020/6/9 17:44
 */
public abstract class AbstractHandleFunctionTemplate extends AbstractTemplate {

    protected TemplateFileClassInfo templateFileClassInfo;

    private Cache resolverResultCache=new CacheLocalLruImpl(156);

    protected ResolverStrategy resolverStrategy;

    public AbstractHandleFunctionTemplate(String templeFileName) throws CodeConfigException, IOException {
        super(templeFileName);
        refresh();
    }


    public AbstractHandleFunctionTemplate(String templeFileName, Environment environment) throws CodeConfigException, IOException {
        super(templeFileName,environment);
        refresh();
    }

    @Override
    public void refresh() throws IOException {
        resolverResultCache.clear();
        if(null!=getTemplateFile()) {
            String templateFileContent = readTemplateFile();
            this.templateFileClassInfo = new TemplateFileClassInfo(getPrefix(templateFileContent), getSuffix(templateFileContent), getFunctionS(templateFileContent));
        }
    }


    /**
     * 获取根据模板最终生成的模板内容
     *
     * @return
     */
    @Override
    public String getTemplateResult() throws TemplateResolveException {
        CacheKey cacheKey=new CacheKey(getTemplateName(),getTemplateVariables());
        TemplateFileClassInfo resultCache= (TemplateFileClassInfo) resolverResultCache.get(cacheKey);
        if(null==resultCache) {
            String resultPrefix = this.templateFileClassInfo.getTemplateClassPrefix();
            String resultSuffix = this.templateFileClassInfo.getTemplateClassSuffix();
            Map<String, String> functionS = this.templateFileClassInfo.getFunctionS();

            Iterator<Map.Entry<String, String>> functionIterator = functionS.entrySet().iterator();
            //清除前缀和后缀的{{}}代码
            resultPrefix = getTemplateResolver().resolver(resultPrefix, getTemplateVariables());
            resultSuffix = getTemplateResolver().resolver(resultSuffix, getTemplateVariables());

            //清除方法名和方法体的{{}}代码
            Map<String, String> tempFunctionMap = new LinkedHashMap<>(functionS.size());
            while (functionIterator.hasNext()) {
                Map.Entry<String, String> entry = functionIterator.next();
                String functionName = entry.getKey();
                String functionBody = entry.getValue().replaceAll("\\" + AbstractTemplateResolver.FUNCTION_NAME_BETWEEN_SPLIT, "");
                functionName = getNoResolverFunctionName(functionName);
                functionBody = getTemplateResolver().resolver(functionBody, getTemplateVariables());
                if (!tempFunctionMap.containsKey(functionName)) {
                    tempFunctionMap.put(functionName, functionBody);
                }
            }
            resultCache=new TemplateFileClassInfo(resultPrefix,resultSuffix,tempFunctionMap);
            resolverResultCache.put(cacheKey,resultCache);
        }


        //解析策略
        if(null!=resolverStrategy) {
            resolverStrategy.resolverStrategy(resultCache);
        }

        Map<String, String> tempFunctionMap =resultCache.getFunctionS();
        StringBuilder functionStr = new StringBuilder();
        tempFunctionMap.forEach((k, v) -> {
            functionStr.append(v);
        });

        return resultCache.getTemplateClassPrefix() + functionStr.toString() + resultCache.getTemplateClassSuffix();
    }




    /**
     * 获取模板内容的后缀
     *
     * @param templateContent 模板内容
     * @return 模板内容的后缀
     */
    public String getSuffix(String templateContent) {
        Matcher matcher = AbstractTemplateResolver.templateSuffixPattern.matcher(templateContent);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    /**
     * 获取模板内容的前缀
     *
     * @param templateContent 模板内容
     * @return 模板内容的后缀
     */
    public String getPrefix(String templateContent) {
        Matcher matcher = AbstractTemplateResolver.templatePefixPattern.matcher(templateContent);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }


    /**
     * 获取模板方法的内容
     *
     * @param templateContent 模板内容
     * @return 模板方法的内容
     */
    public Map<String, String> getFunctionS(String templateContent) {
        Matcher matcher = AbstractTemplateResolver.templateFunctionBodyPattern.matcher(templateContent);
        Map<String, String> functions = new LinkedHashMap<>(10);
        while (matcher.find()) {
            String group = matcher.group("title");
            Matcher functionNameMather = AbstractTemplateResolver.templateFunctionNamePattern.matcher(group);
            String functionName = functionNameMather.find() ? functionNameMather.group() : "";
            if (!Utils.isEmpty(functionName)) {
                functions.put(functionName, group);
            }
        }
        return functions;
    }

    public TemplateFileClassInfo getTemplateFileClassInfo() {
        return templateFileClassInfo;
    }

    /**
     * 设置模板解析策略
     * @param resolverStrategy 模板解析策略
     */
    public abstract void setResolverStrategy(ResolverStrategy resolverStrategy);

    /**
     * 获取所有的方法名
     * @return
     */
    public Set<String> getTemplateFunctionNameS() throws TemplateResolveException {
        Set<String> functionNameS=new HashSet<>(templateFileClassInfo.getFunctionS().keySet().size());
        for(String functionName:templateFileClassInfo.getFunctionS().keySet()){
            functionNameS.add(getNoResolverFunctionName(functionName));
        }
        return functionNameS;
    }

    public String getNoResolverFunctionName(String srcFunctionName) {
        Matcher matcher=AbstractTemplateResolver.templateFunctionNamePefixSuffixPattern.matcher(srcFunctionName);
        if(matcher.find()){
            String prefix=matcher.group("bodyPrefix");
            String suffix=matcher.group("bodySuffix");
            return prefix+suffix;
        }else{
            return srcFunctionName;
        }
    }
}