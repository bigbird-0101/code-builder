package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.cache.impl.SimpleCacheImpl;
import io.github.bigbird0101.code.core.exception.CodeBuilderException;
import io.github.bigbird0101.code.core.cache.Cache;
import io.github.bigbird0101.code.core.cache.CacheKey;
import io.github.bigbird0101.code.exception.TemplateResolveException;

import java.io.IOException;

/**
 * 不需要操作方法的模板
 * @author fpp
 * @version 1.0
 */
public abstract class AbstractNoHandleFunctionTemplate extends AbstractTemplate {

    private String templateContent;

    private final Cache<CacheKey,String> resolverResultCache=new SimpleCacheImpl<>(156);

    protected volatile boolean refreshed;

    @Override
    public void refresh(){
        resolverResultCache.clear();
        try {
            this.templateContent = readTemplateFile();
        } catch (IOException e) {
            throw new CodeBuilderException(e);
        }
        refreshed=true;
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
        if(!refreshed){
            this.refresh();
        }
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
