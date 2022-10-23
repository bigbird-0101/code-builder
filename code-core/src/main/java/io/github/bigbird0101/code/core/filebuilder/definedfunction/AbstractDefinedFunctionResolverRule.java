package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import io.github.bigbird0101.code.core.template.AbstractHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.util.Utils;

/**
 * @author Administrator
 */
public abstract class AbstractDefinedFunctionResolverRule implements DefinedFunctionResolverRule {
    public static final String IS_INTERFACE = "(?<outer>.*)(?<=private|public|protected)\\s+interface\\s+(?<className>.*?)\\s*\\{\\s*(?<outer3>.*)";
    private RepresentFactorReplaceRuleResolver representFactorReplaceRuleResolver;

    public AbstractDefinedFunctionResolverRule() {
    }

    public AbstractDefinedFunctionResolverRule(RepresentFactorReplaceRuleResolver representFactorReplaceRuleResolver) {
        this.representFactorReplaceRuleResolver = representFactorReplaceRuleResolver;
    }

    public RepresentFactorReplaceRuleResolver getRepresentFactorReplaceRuleResolver() {
        return representFactorReplaceRuleResolver;
    }

    public void setRepresentFactorReplaceRuleResolver(RepresentFactorReplaceRuleResolver representFactorReplaceRuleResolver) {
        this.representFactorReplaceRuleResolver = representFactorReplaceRuleResolver;
    }

    /**
     * 是否是接口
     * @param template
     * @return
     */
    public boolean isInterface(Template template){
        if(template instanceof AbstractHandleFunctionTemplate){
            AbstractHandleFunctionTemplate abstractHandleFunctionTemplate= (AbstractHandleFunctionTemplate) template;
            final String templateClassPrefix = abstractHandleFunctionTemplate.getTemplateFileClassInfoWhenResolved().getTemplateClassPrefix();
            return Utils.getIgnoreLowerUpperMather(templateClassPrefix, IS_INTERFACE).find();
        }
        return false;
    }
}
