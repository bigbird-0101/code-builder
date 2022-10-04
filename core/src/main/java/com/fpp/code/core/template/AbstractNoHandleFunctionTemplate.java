package com.fpp.code.core.template;

import com.fpp.code.core.cache.impl.SimpleCacheImpl;
import com.fpp.code.core.exception.CodeBuilderException;
import com.fpp.code.core.cache.Cache;
import com.fpp.code.core.cache.CacheKey;
import com.fpp.code.exception.TemplateResolveException;

import java.io.IOException;

/**
 * 不需要操作方法的模板
 * @author fpp
 * @version 1.0
 * @date 2020/6/9 17:45
 */
public abstract class AbstractNoHandleFunctionTemplate extends AbstractTemplate {

    private String templateContent;

    private final Cache<CacheKey,String> resolverResultCache=new SimpleCacheImpl<>(156);

    @Override
    public void refresh(){
        resolverResultCache.clear();
        try {
            this.templateContent = readTemplateFile();
        } catch (IOException e) {
            throw new CodeBuilderException(e);
        }
    }


    @Override
    public String getTemplateResult() throws TemplateResolveException {
        doResolverTemplateBefore();
        final String result = doResolverTemplate();
        return doResolverTemplateAfter(result);
    }

    /**
     * 解析模板之后
     * @param result
     * @return
     */
    protected String doResolverTemplateAfter(String result) {
        return result;
    }

    /**
     * 解析模板之前
     */
    protected void doResolverTemplateBefore() {
        initTemplateVariables();
    }

    /**
     * 解析模板
     * @return
     */
    protected String doResolverTemplate() {
        CacheKey cacheKey=new CacheKey(getTemplateName(),getTemplateVariables());
        String result=resolverResultCache.get(cacheKey);
        if(null==result) {
            result = doBuildTemplateResultCache();
            resolverResultCache.put(cacheKey,result);
        }
        return result;
    }

    protected String doBuildTemplateResultCache() {
        return getTemplateResolver().resolver(this.templateContent, getTemplateVariables());
    }

}
