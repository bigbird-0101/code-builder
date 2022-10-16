package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.ReUtil;
import io.github.bigbird0101.code.core.domain.DefinedFunctionDomain;
import io.github.bigbird0101.code.core.template.TemplateTraceContext;
import io.github.bigbird0101.code.util.Utils;

import java.util.regex.Matcher;

/**
 * java 方法体之外解析规则
 *
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 9:40
 */
public class JavaFunctionBodyOuterDefinedFunctionResolverRule extends AbstractDefinedFunctionResolverRule {

    /**
     * 将模板方法根据规则解析成自定义方法
     *
     * @param definedFunctionDomain
     * @return 解析后自定义方法
     */
    @Override
    public String doRule(DefinedFunctionDomain definedFunctionDomain) {
        String definedValue = definedFunctionDomain.getDefinedValue();
        String representFactor = definedFunctionDomain.getRepresentFactor();
        String srcFunctionBody = definedFunctionDomain.getTemplateFunction();
        final boolean isInterface = isInterface(TemplateTraceContext.getCurrentTemplate());
        Matcher matcher =isInterface?INTERFACE_FUNCTION.matcher(srcFunctionBody):FUNCTION.matcher(srcFunctionBody);
        if(matcher.find()){
            final String group = matcher.group(FUNCTION_BODY_OUTER);
            final String[] splits = group.split("\r\n|\n");
            for(String line:splits){
                srcFunctionBody = Utils.getIgnoreLowerUpperMather(srcFunctionBody, ReUtil.escape(line))
                    .replaceAll(ReUtil.escape(getRepresentFactorReplaceRuleResolver().doResolver(line,representFactor,definedValue)));
            }
        }
        return srcFunctionBody;
    }
}
