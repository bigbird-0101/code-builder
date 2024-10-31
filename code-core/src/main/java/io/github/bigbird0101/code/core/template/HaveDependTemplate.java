package io.github.bigbird0101.code.core.template;

import cn.hutool.core.collection.CollectionUtil;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 有依赖的模板
 * @author Administrator
 */
public interface HaveDependTemplate extends Template {
    /**
     * 获取依赖的模板名
     * @return
     */
    Set<String> getDependTemplates();

    static void updateDependTemplate(DefaultListableTemplateFactory operateTemplateBeanFactory, String templateNameSrc, String templateNameNewSrc) {
        operateTemplateBeanFactory.getTemplateNames()
                .stream()
                .map(operateTemplateBeanFactory::getTemplate)
                .filter(s -> s instanceof HaveDependTemplate)
                .map(s -> (HaveDependTemplate) s)
                .forEach(s -> {
                    Set<String> templates = s.getDependTemplates();
                    if (CollectionUtil.isNotEmpty(templates)) {
                        replace(templates, templateNameSrc, templateNameNewSrc);
                    }
                    operateTemplateBeanFactory.refreshTemplate(s);
                });
    }

    static void replace(Set<String> sets, String old, String newValue) {
        // 创建一个新的Set来存储结果
        Set<String> updatedSet = new LinkedHashSet<>();
        // 遍历原始的Set，替换元素
        for (String item : sets) {
            if (item.equals(old)) {
                updatedSet.add(newValue);
            } else {
                updatedSet.add(item);
            }
        }
        sets.clear();
        sets.addAll(updatedSet);
    }
}
