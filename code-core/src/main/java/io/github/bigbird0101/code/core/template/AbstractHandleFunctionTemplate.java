package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.ObjectUtil;
import io.github.bigbird0101.code.core.cache.Cache;
import io.github.bigbird0101.code.core.cache.CacheKey;
import io.github.bigbird0101.code.core.cache.CachePool;
import io.github.bigbird0101.code.core.domain.DefinedFunctionDomain;
import io.github.bigbird0101.code.core.domain.TemplateFileClassInfo;
import io.github.bigbird0101.code.core.exception.CodeBuilderException;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import io.github.bigbird0101.code.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * 需要对方法进行操作的模板
 * @author fpp
 * @version 1.0
 */
public abstract class AbstractHandleFunctionTemplate extends AbstractTemplate {
    private static final Logger LOGGER = LogManager.getLogger(AbstractHandleFunctionTemplate.class);

    protected TemplateFileClassInfo templateFileClassInfoNoResolve;
    protected TemplateFileClassInfo templateFileClassInfoResolved;

    protected final Cache<CacheKey,TemplateFileClassInfo> resolverResultCache= CachePool.build(156);

    protected ResolverStrategy resolverStrategy;

    protected volatile boolean refreshed;

    @Override
    public void refresh(){
        if(null!= getTemplateResource()) {
            String templateFileContent;
            try {
                templateFileContent = readTemplateFile();
            } catch (IOException e) {
                throw new CodeBuilderException(e);
            }
            this.templateFileClassInfoNoResolve = new TemplateFileClassInfo(getPrefix(templateFileContent), getSuffix(templateFileContent), getFunctionS(templateFileContent));
        }
        resolverResultCache.clear();
        deepClearCache();
        refreshed=true;
    }

    protected void deepClearCache() {
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
    public String process(Map<String,Object> dataModel) throws TemplateResolveException {
        LOGGER.info("process:{}  dataModel:{}",getTemplateName(),dataModel);
        doResolverTemplateBefore();
        TemplateFileClassInfo resultCache = doResolverTemplate(dataModel);
        return doResolverTemplateAfter(resultCache,dataModel);
    }

    /**
     * 解析模板之前
     */
    protected void doResolverTemplateBefore() {
        if(!refreshed){
            this.refresh();
        }
    }

    /**
     * 解析模板成功之后
     *
     * @param resultCache
     * @param dataModel
     * @return
     */
    protected String doResolverTemplateAfter(TemplateFileClassInfo resultCache, Map<String, Object> dataModel) {
        //这里深度克隆一下对象 如果不克隆 直接传递引用 缓存将会被修改
        TemplateFileClassInfo tempResultCache= ObjectUtil.cloneByStream(resultCache);
        //解析策略
        if(null!=resolverStrategy) {
            resolverStrategy.resolverStrategy(tempResultCache,dataModel);
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
    protected TemplateFileClassInfo doResolverTemplate(Map<String, Object> dataModel) {
        CacheKey cacheKey=new CacheKey(getTemplateName(),dataModel);
        TemplateFileClassInfo resultCache= resolverResultCache.get(cacheKey);
        LOGGER.info("cacheKey is {}",cacheKey);
        LOGGER.info("cache is {}",resultCache);
        if(null==resultCache) {
            resultCache = doBuildTemplateResultCache(cacheKey,dataModel);
            resolverResultCache.put(cacheKey,resultCache);
        }
        return resultCache;
    }

    protected TemplateFileClassInfo doBuildTemplateResultCache(CacheKey cacheKey, Map<String, Object> dataModel) {
        TemplateFileClassInfo resultCache;
        String resultPrefix = this.templateFileClassInfoNoResolve.getTemplateClassPrefix();
        String resultSuffix = this.templateFileClassInfoNoResolve.getTemplateClassSuffix();
        Map<String, String> functionS = this.templateFileClassInfoNoResolve.getFunctionS();

        Iterator<Map.Entry<String, String>> functionIterator = functionS.entrySet().iterator();
        //清除前缀和后缀的{{}}代码
        resultPrefix = getTemplateResolver().resolver(resultPrefix, dataModel);
        resultSuffix = getTemplateResolver().resolver(resultSuffix, dataModel);

        //清除方法名和方法体的{{}}代码
        Map<String, String> tempFunctionMap = new LinkedHashMap<>(functionS.size());
        while (functionIterator.hasNext()) {
            Map.Entry<String, String> entry = functionIterator.next();
            String functionName = entry.getKey();
            String functionBody = entry.getValue().replaceAll("\\" + AbstractTemplateResolver.FUNCTION_NAME_BETWEEN_SPLIT, "");
            functionName = getNoResolverFunctionName(functionName);
            functionBody = getTemplateResolver().resolver(functionBody, dataModel);
            if (!tempFunctionMap.containsKey(functionName)) {
                tempFunctionMap.put(functionName, functionBody);
            }
        }
        resultCache= new TemplateFileClassInfo(resultPrefix, resultSuffix, tempFunctionMap);
        this.templateFileClassInfoResolved =resultCache;
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
        Matcher matcher = AbstractTemplateResolver.templatePrefixPattern.matcher(templateContent);
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
        Matcher matcher=AbstractTemplateResolver.templateFunctionNamePrefixSuffixPattern.matcher(srcFunctionName);
        if(matcher.find()){
            String prefix=matcher.group("bodyPrefix");
            String suffix=matcher.group("bodySuffix");
            return prefix+suffix;
        }else{
            return srcFunctionName;
        }
    }
}