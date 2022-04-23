package com.fpp.code.core.filebuilder.definedfunction;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.fpp.code.core.cache.Cache;
import com.fpp.code.core.cache.CachePool;
import com.fpp.code.core.domain.DefinedFunctionDomain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 9:20
 */
public abstract class AbstractDefinedFunctionResolver implements DefinedFunctionResolver {

    protected List<DefinedFunctionResolverRule> ruleList=new ArrayList<>(10);
    private static Cache<DefinedFunctionDomain, String> definedFunctionDomainStringCache= CachePool.build(156);

    @Override
    public void addResolverRule(DefinedFunctionResolverRule definedFunctionResolverRule) {
        this.ruleList.add(definedFunctionResolverRule);
    }

    @Override
    public String doResolver(DefinedFunctionDomain definedFunctionDomain) {
        final String cache = definedFunctionDomainStringCache.get(definedFunctionDomain);
        if(StrUtil.isNotBlank(cache)){
            StaticLog.info("AbstractDefinedFunctionResolver doResolver get cache {}",cache);
            return cache;
        }
        final DefinedFunctionDomain clone = (DefinedFunctionDomain) definedFunctionDomain.clone();
        for(DefinedFunctionResolverRule rule:ruleList){
            definedFunctionDomain.setTemplateFunction(rule.doRule(definedFunctionDomain));
        }
        final String templateFunction = definedFunctionDomain.getTemplateFunction();
        definedFunctionDomainStringCache.put(clone, templateFunction);
        return templateFunction;
    }
}
