package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.template.resolver.DependTemplateLangResolver;
import io.github.bigbird0101.code.core.template.resolver.ToolTemplateLangResolver;
import io.github.bigbird0101.code.exception.TemplateResolveException;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * 简单的模板解析器
 * 模板中只包含 简单的 *{}* 和 tool 语法 和 depend语法 不包含其他的 语法
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 17:32:08
 */
public class SimpleAbstractTemplateResolver extends DefaultAbstractTemplateResolver {
    @Override
    protected String doResolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException {
        String tempResult=srcData;
        final List<TemplateLangResolver> templateLangResolverList = getTemplateLangResolverList()
                .stream().filter(s->((s instanceof ToolTemplateLangResolver)||s instanceof DependTemplateLangResolver)).collect(toList());
        for (TemplateLangResolver resolver: templateLangResolverList){
            if(resolver.matchLangResolver(tempResult)){
                tempResult= resolver.langResolver(tempResult, replaceKeyValue);
            }
        }
        return tempResult;
    }

    public static SimpleAbstractTemplateResolver getInstance() {
        return new SimpleAbstractTemplateResolver();
    }
}
