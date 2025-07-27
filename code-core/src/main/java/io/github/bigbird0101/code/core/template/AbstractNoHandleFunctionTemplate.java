package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.cache.Cache;
import io.github.bigbird0101.code.core.cache.CacheKey;
import io.github.bigbird0101.code.core.cache.impl.SimpleCacheImpl;
import io.github.bigbird0101.code.core.exception.CodeBuilderException;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * 不需要操作方法的模板
 * @author fpp
 * @version 1.0
 */
public abstract class AbstractNoHandleFunctionTemplate extends AbstractTemplate {
    private static final Logger LOGGER = LogManager.getLogger(AbstractNoHandleFunctionTemplate.class);

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
    public String process(Map<String,Object> dataModel) throws TemplateResolveException {
        LOGGER.info("process:{}  dataModel:{}",getTemplateName(),dataModel);
        doResolverTemplateBefore(dataModel);
        final String result = doResolverTemplate(dataModel);
        return doResolverTemplateAfter(result);
    }

    /**
     * 解析模板之后
     * @param result result
     * @return 解析结果
     */
    protected String doResolverTemplateAfter(String result) {
        return result;
    }

    /**
     * 解析模板之前
     */
    protected void doResolverTemplateBefore(Map<String, Object> dataModel) {
        if(!refreshed){
            this.refresh();
        }
    }

    /**
     * 解析模板
     * @return 解析结果
     */
    protected String doResolverTemplate(Map<String, Object> dataModel) {
        CacheKey cacheKey=new CacheKey(getTemplateName(),dataModel);
        String result=resolverResultCache.get(cacheKey);
        if(null==result) {
            result = doBuildTemplateResultCache(dataModel);
            resolverResultCache.put(cacheKey,result);
        }
        return result;
    }

    protected String doBuildTemplateResultCache(Map<String, Object> dataModel) {
        return getTemplateResolver().resolver(this.templateContent, dataModel);
    }

}
