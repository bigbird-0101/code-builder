package io.github.bigbird0101.code.core.factory.config;

import io.github.bigbird0101.code.core.template.MultipleTemplate;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Administrator
 */
public interface MultipleTemplateDefinition extends Cloneable, Serializable {
    /**
     * 获取模板名称
     *
     * @return 模板名称
     */
    Set<String> getTemplateNames();

    /**
     * 刷新模板定义
     *
     * @param multipleTemplate 模板
     */
    default void refresh(MultipleTemplate multipleTemplate) {
    }
}
