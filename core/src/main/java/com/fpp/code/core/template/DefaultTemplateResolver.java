package com.fpp.code.core.template;

import com.fpp.code.core.config.Environment;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.exception.TemplateResolveException;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 默认模板解析器
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 13:30
 */
public class DefaultTemplateResolver extends AbstractTemplateResolver{


    public DefaultTemplateResolver() throws CodeConfigException {
        super();
    }

    public DefaultTemplateResolver(Environment environment) throws CodeConfigException {
        super(environment);
    }

    /**
     * 模板语言解析方法
     *
     * @param srcData         需要解析的模板数据
     * @param replaceKeyValue 模板中的变量数据
     * @return 解析后的字符串
     * @throws IllegalAccessException 解析其中的对象字段异常
     * @throws NoSuchFieldException   解析其中的对象字段异常
     */
    @Override
    public String resolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException {
        srcData=srcData.replaceAll(AbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX+AbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX,"");
        String tempResult=this.replace(replaceKeyValue, doResolver(srcData,replaceKeyValue));
        Set<String>  templateVariableKeySet=getTemplateVariableKey(tempResult);
        Iterator<Map.Entry<String, Object>> iterator = replaceKeyValue.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            String key = next.getKey();
            Object value = next.getValue();
            tempResult=analysisBody(templateVariableKeySet, key, value, tempResult);
        }
        return tempResult;
    }

    private String doResolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException {
         String tempResult=srcData;
        for (TemplateLangResolver resolver:getTemplateLangResolverList()){
            if(resolver.matchLangResolver(tempResult)){
                tempResult= resolver.langResolver(tempResult, replaceKeyValue);
            }
        }
        return tempResult;
    }

}
