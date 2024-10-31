package io.github.bigbird0101.code.core.template;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 多个模板集合 组合
 * @author fpp
 * @version 1.0
 */
public interface MultipleTemplate extends Cloneable, Serializable {
    /**
     * 获取模板名称
     * @return
     */
    String getTemplateName();

    /**
     * 设置模板名称
     * @param templateName templateName
     */
    void setTemplateName(String templateName);
    /**
     * 获取多个模板集合
     * @return 模板集合
     */
    Set<Template> getTemplates();

    /**
     * 设置模板集合
     * @param templates templates
     */
    void setTemplates(Set<Template> templates);

    /**
     * 设置模板解析策略
     * @param templateResolverStrategy 模板解析策略  k-为模板名 键值为模板解析策略
     */
    default void setResolverStrategies(Map<String, ResolverStrategy> templateResolverStrategy) {

    }
}
