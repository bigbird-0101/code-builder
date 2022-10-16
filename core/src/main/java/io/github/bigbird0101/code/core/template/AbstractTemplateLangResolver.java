package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import io.github.bigbird0101.code.util.Utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

/**
 * 模板语言解析器
 * @author fpp
 * @version 1.0
 * @date 2020/6/9 16:48
 */
public abstract class AbstractTemplateLangResolver implements TemplateLangResolver, Serializable {
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

    @Override
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
     * @return 获取 语法语句中的 body中的解析后的结果
     * @throws TemplateResolveException TemplateResolveException
     */
    protected String getLangBodyResult(Object targetObject, String body, String targetObjectKey) throws TemplateResolveException {
        StringBuilder stringBuilder=new StringBuilder();
        String tempStamp= Utils.getFirstNewLineNull(body);
        String lastNewLineNull = Utils.getLastNewLineNull(body);
        if(targetObject instanceof Collection){
            Collection<?> collection= (Collection<?>) targetObject;
            for(Object object:collection){
                HashMap<String,Object> replaceKey=new HashMap<>();
                replaceKey.put(targetObjectKey,object);
                String replaceResult=this.templateResolver.resolver(body,replaceKey);
                if(StrUtil.isNotBlank(replaceResult.trim())) {
                    stringBuilder.append(tempStamp)
                            .append(StrUtil.removeSuffix(StrUtil.removePrefix(replaceResult,tempStamp),lastNewLineNull));
                    if(!lastNewLineNull.contains(StrUtil.LF)) {
                        stringBuilder.append(lastNewLineNull);
                    }
                }
            }
        }else{
            HashMap<String,Object> replaceKey=new HashMap<>();
            replaceKey.put(targetObjectKey,targetObject);
            String replaceResult=this.templateResolver.resolver(body,replaceKey);
            if(StrUtil.isNotBlank(replaceResult.trim())) {
                stringBuilder.append(tempStamp)
                        .append(StrUtil.removeSuffix(StrUtil.removePrefix(replaceResult,tempStamp),lastNewLineNull));
                if(!lastNewLineNull.contains(StrUtil.LF)) {
                    stringBuilder.append(lastNewLineNull);
                }
            }
        }
        return stringBuilder.toString();
    }



}
