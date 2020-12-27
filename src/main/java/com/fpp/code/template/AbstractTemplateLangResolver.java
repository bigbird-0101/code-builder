package com.fpp.code.template;

import com.fpp.code.common.Utils;

import java.util.Collection;
import java.util.Set;

/**
 * 模板语言解析器
 * @author fpp
 * @version 1.0
 * @date 2020/6/9 16:48
 */
public abstract class AbstractTemplateLangResolver implements TemplateLangResolver{
    protected String resolverName;
    private TemplateResolver templateResolver;

    public String getResolverName() {
        return resolverName;
    }

    public void setResolverName(String resolverName) {
        this.resolverName = resolverName;
    }

    public TemplateResolver getTemplateResolver() {
        return templateResolver;
    }

    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    public AbstractTemplateLangResolver() {
    }

    public AbstractTemplateLangResolver(TemplateResolver templateResolver) {
        this.templateResolver=templateResolver;
    }

    /**
     * 获取 语法语句中的 body中的解析后的结果
     * @param targetObject 语法中目标对象
     * @param body 语法中的语法题
     * @param targetObjectKey 语法中所包含的对象key
     * @return
     * @throws IllegalAccessException
     */
    protected String getLangBodyResult(Object targetObject, String body, String targetObjectKey) throws TemplateResolveException {
        StringBuilder stringBuilder=new StringBuilder();
        Set<String> set=this.templateResolver.getTemplateVariableKey(body);
        String tempStamp= Utils.getFirstNewLineNull(body);
        if(targetObject instanceof Collection){
            Collection collection= (Collection) targetObject;
            for(Object object:collection){
                String replaceResult=this.templateResolver.analysisBody(set, targetObjectKey,object,body);
                stringBuilder.append(tempStamp+replaceResult.trim());
            }
        }else{
            String replaceResult=this.templateResolver.analysisBody(set, targetObjectKey,targetObject,body);
            stringBuilder.append(tempStamp+replaceResult.trim());
        }
        return stringBuilder.toString();
    }



}
