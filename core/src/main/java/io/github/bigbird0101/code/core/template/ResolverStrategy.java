package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.domain.TemplateFileClassInfo;

/**
 * 解析策略
 * @author fpp
 * @version 1.0
 */
@FunctionalInterface
public interface ResolverStrategy {
    /**
     * 解析策略
     * @param templateFileClassInfo 模板的详情信息
     */
   void resolverStrategy(TemplateFileClassInfo templateFileClassInfo);
}
