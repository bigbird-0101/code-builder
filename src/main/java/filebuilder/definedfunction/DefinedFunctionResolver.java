package main.java.filebuilder.definedfunction;

import main.java.orgv2.DefinedFunctionDomain;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 8:57
 */
public interface DefinedFunctionResolver {
    /**
     * 将模板方法解析成自定义方法
     * @param definedFunctionDomain 自定义方法domain
     * @return 解析后的自定义方法
     */
    String doResolver(DefinedFunctionDomain definedFunctionDomain);

    /**
     * 添加解析规则
     * @param definedFunctionResolverRule 自定义方法解析器的解析规则
     */
    void addResolverRule(DefinedFunctionResolverRule definedFunctionResolverRule);
}
