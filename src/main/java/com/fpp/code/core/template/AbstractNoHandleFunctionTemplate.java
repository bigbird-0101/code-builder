package com.fpp.code.core.template;

import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.template.cache.Cache;
import com.fpp.code.core.template.cache.CacheKey;
import com.fpp.code.core.template.cache.impl.CacheLocalLruImpl;

import java.util.Map;

/**
 * 不需要操作方法的模板
 * @author fpp
 * @version 1.0
 * @date 2020/6/9 17:45
 */
public abstract class AbstractNoHandleFunctionTemplate extends AbstractTemplate {

    private String templateContent;

    private Cache resolverResultCache=new CacheLocalLruImpl(156);

    public AbstractNoHandleFunctionTemplate(String templeFileName, Environment environment) throws CodeConfigException {
        super(templeFileName,environment);
        refresh();
    }

    public AbstractNoHandleFunctionTemplate(String templeFileName) throws CodeConfigException {
        super(templeFileName);
        refresh();
    }

    @Override
    public void refresh() {
        resolverResultCache.clear();
        this.templateContent = readTemplateFile();
    }


    @Override
    public String getTempleResult(Map<String, Object> replaceKeyValue) throws TemplateResolveException {
        CacheKey cacheKey=new CacheKey(getTemplateName(),replaceKeyValue);
        Object result=resolverResultCache.get(cacheKey);
        if(null==result) {
            result = getTemplateResolver().resolver(this.templateContent, replaceKeyValue);
            resolverResultCache.put(cacheKey,result);
        }
        return (String) result;
    }

}
