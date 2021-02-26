package com.fpp.code.core.filebuilder.definedfunction;

import com.fpp.code.core.domain.DefinedFunctionDomain;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 9:03
 */
public interface DefinedFunctionResolverRule {
    /**
     * 将模板方法根据规则解析成自定义方法
     * @param definedFunctionDomain
     * @return 解析后自定义方法
     */
    String doRule(DefinedFunctionDomain definedFunctionDomain);
}
