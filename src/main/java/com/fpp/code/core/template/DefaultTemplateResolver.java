package com.fpp.code.core.template;

import com.fpp.code.common.Utils;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.Environment;

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
        String tempResult=this.replace(replaceKeyValue, doResolver(srcData,replaceKeyValue));
        Set<String>  templateVariableKeySet=getTemplateVariableKey(tempResult);
        return analysisBody(templateVariableKeySet,"tableInfo",replaceKeyValue.get("tableInfo") ,tempResult);
    }

    private String doResolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException {
         String tempResult="";
        for (TemplateLangResolver resolver:getTemplateLangResolverList()){
            tempResult=resolver.langResolver(Utils.isEmpty(tempResult)?srcData:tempResult,replaceKeyValue);
        }
        return tempResult;
    }

}
