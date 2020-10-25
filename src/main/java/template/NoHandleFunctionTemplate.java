package main.java.template;

import main.java.common.Utils;
import main.java.config.CodeConfigException;
import main.java.config.ProjectFileConfig;
import main.java.template.cache.Cache;
import main.java.template.cache.CacheKey;
import main.java.template.cache.impl.CacheLocalLruImpl;

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
        refresh(templeFileName);
    }

    public NoHandleFunctionTemplate(String templeFileName) throws IOException, CodeConfigException {
        super(templeFileName);
        refresh(templeFileName);
    }

    @Override
    public void refresh(String templeFileName) throws IOException {
        resolverResultCache.clear();
        if(Utils.isNotEmpty(templeFileName)) {
            this.templateContent = readTempleteFile(templeFileName);
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
