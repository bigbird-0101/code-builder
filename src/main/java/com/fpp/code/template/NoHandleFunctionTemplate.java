package com.fpp.code.template;

import com.fpp.code.common.Utils;
import com.fpp.code.config.CodeConfigException;
import com.fpp.code.config.ProjectFileConfig;
import com.fpp.code.template.cache.Cache;
import com.fpp.code.template.cache.CacheKey;
import com.fpp.code.template.cache.impl.CacheLocalLruImpl;

import java.io.IOException;
import java.util.Map;

/**
 * 不需要操作方法的模板
 * @author fpp
 * @version 1.0
 * @date 2020/6/9 17:45
 */
public abstract class NoHandleFunctionTemplate extends AbstractTemplate {
    private String templateContent;

    private Cache resolverResultCache=new CacheLocalLruImpl(156);

    public NoHandleFunctionTemplate(String templeFileName, ProjectFileConfig projectFileConfig) throws IOException, CodeConfigException {
        super(templeFileName,projectFileConfig);
        refresh();
    }

    public NoHandleFunctionTemplate(String templeFileName) throws IOException, CodeConfigException {
        super(templeFileName);
        refresh();
    }

    @Override
    public void refresh() {
        resolverResultCache.clear();
        if(Utils.isNotEmpty(getTemlateFilePathUrl())) {
            this.templateContent = readTempleteFile(getTemlateFilePathUrl());
        }
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
