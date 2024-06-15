package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.spi.order.OrderAware;

import java.io.File;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-05 23:29:37
 */
public interface TemplateMatchRule extends OrderAware {
    /**
     * 模板文件是否匹配该模板
     * @param file 模板文件
     * @return 是否匹配该模板 true-是 false-否
     */
    boolean match(File file);

    /**
     * 匹配的优先级 数字越大优先级越低
     * @return
     */
    @Override
    default int getOrder(){
        return Integer.MAX_VALUE;
    }
}
