package com.fpp.code.core.template;

import com.fpp.code.core.exception.CodeBuilderException;
import com.fpp.code.core.template.cache.Cache;
import com.fpp.code.core.template.cache.CacheKey;
import com.fpp.code.core.template.cache.impl.CacheLocalLruImpl;

import java.io.IOException;

/**
 * 不需要操作方法的模板
 * @author fpp
 * @version 1.0
 * @date 2020/6/9 17:45
 */
public abstract class AbstractNoHandleFunctionTemplate extends AbstractTemplate {

    private String templateContent;

    private Cache resolverResultCache=new CacheLocalLruImpl(156);

    public AbstractNoHandleFunctionTemplate(String templeFileName){
        super(templeFileName);
        refresh();
    }

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
        CacheKey cacheKey=new CacheKey(getTemplateName(),getTemplateVariables());
        Object result=resolverResultCache.get(cacheKey);
        if(null==result) {
            result = getTemplateResolver().resolver(this.templateContent, getTemplateVariables());
            resolverResultCache.put(cacheKey,result);
        }
        return (String) result;
    }

}
