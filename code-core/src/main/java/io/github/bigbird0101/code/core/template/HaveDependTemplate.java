package io.github.bigbird0101.code.core.template;

import cn.hutool.core.collection.CollectionUtil;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 有依赖的模板
 * @author Administrator
 */
public interface HaveDependTemplate extends Template {
    /**
     * 获取依赖的模板名
     * @return 依赖的模板名
     */
    Set<String> getDependTemplates();

    /**
     * 更新模板依赖
     *
     * @param operateTemplateBeanFactory 模板工厂
     * @param templateNameSrc            模板名
     * @param templateNameNewSrc         新模板名
     */
    static void updateDependTemplate(DefaultListableTemplateFactory operateTemplateBeanFactory, String templateNameSrc, String templateNameNewSrc) {
        List<HaveDependTemplate> list = operateTemplateBeanFactory.getTemplateNames()
                .stream()
                .map(operateTemplateBeanFactory::getTemplate)
                .filter(s -> s instanceof HaveDependTemplate)
                .map(s -> (HaveDependTemplate) s)
                .toList();
        list.stream()
                .filter(s -> CollectionUtil.isNotEmpty(s.getDependTemplates())
                        && s.getDependTemplates().contains(templateNameSrc))
                .forEach(s -> {
                    Set<String> templates = s.getDependTemplates();
                    if (CollectionUtil.isNotEmpty(templates) && templates.contains(templateNameSrc)) {
                        replace(templates, templateNameSrc, templateNameNewSrc);
                        TemplateDefinition templateDefinition = operateTemplateBeanFactory.getTemplateDefinition(s.getTemplateName());
                        replace(templateDefinition.getDependTemplates(), templateNameSrc, templateNameNewSrc);
                        operateTemplateBeanFactory.refreshTemplate(s);
                    }
                });
    }

    /**
     * 更新模板依赖
     *
     * @param operateTemplateBeanFactory 模板工厂
     * @param srcNewMap                  srcNewMap
     */
    static void updateDependTemplate(DefaultListableTemplateFactory operateTemplateBeanFactory, Map<String, String> srcNewMap) {
        List<HaveDependTemplate> list = operateTemplateBeanFactory.getTemplateNames()
                .stream()
                .map(operateTemplateBeanFactory::getTemplate)
                .filter(s -> s instanceof HaveDependTemplate)
                .map(s -> (HaveDependTemplate) s)
                .toList();
        list.stream()
                .filter(s -> CollectionUtil.isNotEmpty(s.getDependTemplates())
                        && s.getDependTemplates().stream().anyMatch(srcNewMap::containsKey))
                .forEach(s -> {
                    Set<String> templates = s.getDependTemplates();
                    srcNewMap.forEach((templateNameSrc, templateNameNewSrc) ->
                            replace(templates, templateNameSrc, templateNameNewSrc));
                    TemplateDefinition templateDefinition = operateTemplateBeanFactory.getTemplateDefinition(s.getTemplateName());
                    srcNewMap.forEach((templateNameSrc, templateNameNewSrc) ->
                            replace(templateDefinition.getDependTemplates(), templateNameSrc, templateNameNewSrc));
                    operateTemplateBeanFactory.refreshTemplate(s);
                });
    }

    /**
     * 替换
     * @param sets 集合
     * @param old 旧值
     * @param newValue 新值
     */
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
