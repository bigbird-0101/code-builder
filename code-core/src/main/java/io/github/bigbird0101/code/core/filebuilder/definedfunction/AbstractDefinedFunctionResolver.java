package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import io.github.bigbird0101.code.core.cache.Cache;
import io.github.bigbird0101.code.core.cache.CachePool;
import io.github.bigbird0101.code.core.domain.DefinedFunctionDomain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fpp
 * @version 1.0
 * @since 2020/7/13 9:20
 */
public abstract class AbstractDefinedFunctionResolver implements DefinedFunctionResolver {

    protected List<DefinedFunctionResolverRule> ruleList=new ArrayList<>(10);
    private static final Cache<DefinedFunctionDomain, String> DEFINED_FUNCTION_DOMAIN_STRING_CACHE = CachePool.build(156);

    @Override
    public void addResolverRule(DefinedFunctionResolverRule definedFunctionResolverRule) {
        this.ruleList.add(definedFunctionResolverRule);
    }

    @Override
    public String doResolver(DefinedFunctionDomain definedFunctionDomain) {
        final String cache = DEFINED_FUNCTION_DOMAIN_STRING_CACHE.get(definedFunctionDomain);
        if(StrUtil.isNotBlank(cache)){
            StaticLog.debug("AbstractDefinedFunctionResolver doResolver get cache {}",cache);
            return cache;
        }
        StaticLog.debug("AbstractDefinedFunctionResolver doResolver not get cache {}",definedFunctionDomain);
        final DefinedFunctionDomain clone = (DefinedFunctionDomain) definedFunctionDomain.clone();
        for(DefinedFunctionResolverRule rule:ruleList){
            final long l = System.currentTimeMillis();
            definedFunctionDomain.setTemplateFunction(rule.doRule(definedFunctionDomain));
            final long e = System.currentTimeMillis();
            StaticLog.debug("AbstractDefinedFunctionResolver rule {} {}",rule,(e-l)/1000);
        }
        final String templateFunction = definedFunctionDomain.getTemplateFunction();
        DEFINED_FUNCTION_DOMAIN_STRING_CACHE.put(clone, templateFunction);
        return templateFunction;
    }
}
