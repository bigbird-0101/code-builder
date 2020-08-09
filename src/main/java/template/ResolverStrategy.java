package main.java.template;

/**
 * 解析策略
 * @author fpp
 * @version 1.0
 * @date 2020/6/16 13:50
 */
@FunctionalInterface
public interface ResolverStrategy {
    /**
     * 解析策略
     * @param templateFileClassInfo 模板的详情信息
     */
    void resolverStrategy(TemplateFileClassInfo templateFileClassInfo);
}
