package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import java.util.ServiceLoader;

/**
 * @author Administrator
 */
public class DefaultRepresentFactorReplaceRuleResolver extends AbstractRepresentFactorReplaceRuleResolver{

    public DefaultRepresentFactorReplaceRuleResolver() {
        ServiceLoader.load(RepresentFactorReplaceRule.class).forEach(rule->{
            if(rule instanceof AbstractRepresentFactorReplaceRule){
                AbstractRepresentFactorReplaceRule abstractRepresentFactorReplaceRule= (AbstractRepresentFactorReplaceRule) rule;
                abstractRepresentFactorReplaceRule.setRepresentFactorReplaceRuleResolver(this);
            }
            this.addResolverRule(rule);
        });
    }
}
