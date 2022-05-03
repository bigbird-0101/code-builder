package com.fpp.code.core.filebuilder.definedfunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ServiceLoader;

/**
 * 默认的自定义方法解析器
 * @author fpp
 * @version 1.0
 * @date 2020/7/10 18:38
 */
public class DefaultDefinedFunctionResolver extends AbstractDefinedFunctionResolver {

    private static Logger logger= LogManager.getLogger(DefaultDefinedFunctionResolver.class);

    public DefaultDefinedFunctionResolver() {
        final DefaultRepresentFactorReplaceRuleResolver defaultRepresentFactorReplaceRuleResolver = new DefaultRepresentFactorReplaceRuleResolver();
        ServiceLoader.load(DefinedFunctionResolverRule.class).forEach(rule->{
            if(rule instanceof AbstractDefinedFunctionResolverRule){
                AbstractDefinedFunctionResolverRule abstractDefinedFunctionResolverRule= (AbstractDefinedFunctionResolverRule) rule;
                abstractDefinedFunctionResolverRule.setRepresentFactorReplaceRuleResolver(defaultRepresentFactorReplaceRuleResolver);
            }
            this.addResolverRule(rule);
        });
        if(this.ruleList.size()==0&&logger.isWarnEnabled()){
            logger.warn("defined function resolver rule load failed");
        }
    }
}
