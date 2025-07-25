package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.exception.TemplateResolveException;

import java.util.Map;
import java.util.Set;

/**
 * 默认模板解析器
 * @author fpp
 * @version 1.0
 */
public class DefaultAbstractTemplateResolver extends AbstractHandleAbstractTemplateResolver {


    public DefaultAbstractTemplateResolver() throws CodeConfigException {
        super();
    }

    public DefaultAbstractTemplateResolver(Environment environment) throws CodeConfigException {
        super(environment);
    }

    /**
     * 模板语言解析方法
     *
     * @param srcData         需要解析的模板数据
     * @param replaceKeyValue 模板中的变量数据
     * @return 解析后的字符串
     * @throws TemplateResolveException 解析其中的对象字段异常
     */
    @Override
    public String resolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException {
        srcData = srcData.replaceAll(AbstractAbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX_ESCAPE + AbstractAbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX_ESCAPE, "");
        String tempResult=this.replace(replaceKeyValue, doResolver(srcData,replaceKeyValue));
        Set<String>  templateVariableKeySet=getTemplateVariableKey(tempResult);
        for (Map.Entry<String, Object> next : replaceKeyValue.entrySet()) {
            String key = next.getKey();
            Object value = next.getValue();
            tempResult = analysisBody(templateVariableKeySet, key, value, tempResult);
        }
        return tempResult;
    }

    protected String doResolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException {
         String tempResult=srcData;
        for (TemplateLangResolver resolver:getTemplateLangResolverList()){
            if(resolver.matchLangResolver(tempResult)){
                tempResult= resolver.langResolver(tempResult, replaceKeyValue);
            }
        }
        return tempResult;
    }

}
