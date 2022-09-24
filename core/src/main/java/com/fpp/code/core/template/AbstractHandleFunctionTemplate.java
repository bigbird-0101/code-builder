package com.fpp.code.core.template;

import com.fpp.code.core.cache.Cache;
import com.fpp.code.core.cache.CacheKey;
import com.fpp.code.core.cache.CachePool;
import com.fpp.code.core.common.ObjectUtils;
import com.fpp.code.core.domain.DefinedFunctionDomain;
import com.fpp.code.core.exception.CodeBuilderException;
import com.fpp.code.exception.TemplateResolveException;
import com.fpp.code.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static Logger logger= LogManager.getLogger(AbstractHandleFunctionTemplate.class);

    protected TemplateFileClassInfo templateFileClassInfoNoResolve;
    protected TemplateFileClassInfo templateFileClassInfoResolved;

    private Cache<CacheKey,TemplateFileClassInfo> resolverResultCache= CachePool.build(156);

    protected ResolverStrategy resolverStrategy;

    @Override
    public void refresh(){
        if(null!=getTemplateFile()) {
            String templateFileContent;
            try {
                templateFileContent = readTemplateFile();
            } catch (IOException e) {
                throw new CodeBuilderException(e);
            }
            this.templateFileClassInfoNoResolve = new TemplateFileClassInfo(getPrefix(templateFileContent), getSuffix(templateFileContent), getFunctionS(templateFileContent));
        }
        resolverResultCache.clear();
        this.initTemplateVariables();
        //清除本模板的方法的自定义方法缓存
        CachePool.clear(s->s.getKeys()
                .stream()
                .filter(b->b instanceof DefinedFunctionDomain)
                .map(b->(DefinedFunctionDomain) b)
                .anyMatch(b-> getTemplateFunctionNameS().contains(b.getTemplateFunctionName())));
    }


    /**
     * 获取根据模板最终生成的模板内容
     *
     * @return
     */
    @Override
    public String getTemplateResult() throws TemplateResolveException {
        doResolverTemplateBefore();
        TemplateFileClassInfo resultCache = doResolverTemplate();
        return doResolverTemplateAfter(resultCache);
    }

    /**
     * 解析模板之前
     */
    private void doResolverTemplateBefore() {
        initTemplateVariables();
    }

    /**
     * 解析模板成功之后
     * @param resultCache
     * @return
     */
    private String doResolverTemplateAfter(TemplateFileClassInfo resultCache) {
        //这里深度克隆一下对象 如果不克隆 直接传递引用 缓存将会被修改
        TemplateFileClassInfo tempResultCache= (TemplateFileClassInfo) ObjectUtils.deepClone(resultCache);
        //解析策略
        if(null!=resolverStrategy) {
            resolverStrategy.resolverStrategy(tempResultCache);
        }
        StringBuilder functionStr = new StringBuilder();
        Map<String, String> tempFunctionMap = tempResultCache.getFunctionS();
        tempFunctionMap.forEach((k, v) -> functionStr.append(v));

        return tempResultCache.getTemplateClassPrefix() + functionStr + tempResultCache.getTemplateClassSuffix();
    }

    /**
     * 解析模板
     * @return
     */
    private TemplateFileClassInfo doResolverTemplate() {
        CacheKey cacheKey=new CacheKey(getTemplateName(),getTemplateVariables());
        TemplateFileClassInfo resultCache= resolverResultCache.get(cacheKey);
        logger.info("cacheKey is {}",cacheKey);
        logger.info("cache is {}",resultCache);
        if(null==resultCache) {
            String resultPrefix = this.templateFileClassInfoNoResolve.getTemplateClassPrefix();
            String resultSuffix = this.templateFileClassInfoNoResolve.getTemplateClassSuffix();
            Map<String, String> functionS = this.templateFileClassInfoNoResolve.getFunctionS();

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
            this.templateFileClassInfoResolved =resultCache;
            resolverResultCache.put(cacheKey,resultCache);
        }
        return resultCache;
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

    public TemplateFileClassInfo getTemplateFileClassInfoWhenResolved() {
        return templateFileClassInfoResolved;
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
    public Set<String> getTemplateFunctionNameS() {
        Set<String> functionNameS=new HashSet<>(templateFileClassInfoNoResolve.getFunctionS().keySet().size());
        for(String functionName: templateFileClassInfoNoResolve.getFunctionS().keySet()){
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