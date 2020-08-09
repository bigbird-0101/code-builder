package main.java.filebuilder.definedfunction;

import java.util.ServiceLoader;

/**
 * 默认的自定义方法解析器
 * @author fpp
 * @version 1.0
 * @date 2020/7/10 18:38
 */
public class DefaultDefinedFunctionResolver extends AbstractDefinedFunctionResolver {

    public DefaultDefinedFunctionResolver() {
        ServiceLoader.load(DefinedFunctionResolverRule.class).forEach(this::addResolverRule);
    }
}
