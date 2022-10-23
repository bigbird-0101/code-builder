package io.github.bigbird0101.code.core.factory;

import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinitionRegistry;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinitionRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 默认的模板工厂实现
 *
 * @author Administrator
 */
public class DefaultListableTemplateFactory extends AbstractOperateTemplateTemplateFactory implements TemplateDefinitionRegistry, ConfigurableListableTemplateFactory, MultipleTemplateDefinitionRegistry {

    private final Map<String, TemplateDefinition> templateDefinitionMap = new HashMap<>();
    private final Set<String> templateDefinitionSets = new HashSet<>();

    private Boolean allowTemplateDefinitionOverriding=true;

    private final Map<String, MultipleTemplateDefinition> multipleTemplateDefinitionMap = new HashMap<>();
    private final Set<String> multipleTemplateDefinitionSets = new HashSet<>();

    private Environment environment;

    @Override
    public void registerTemplateDefinition(String templateName, TemplateDefinition templateDefinition) {
        TemplateDefinition templateDefinitionTemp = templateDefinitionMap.get(templateName);
        if (null != templateDefinitionTemp&&!getAllowTemplateDefinitionOverriding()) {
            throw new IllegalStateException("Could not register TemplateDefinition [" + templateDefinition +
                    "] under TemplateDefinition name '" + templateName + "': there is already object [" + templateDefinitionTemp + "] bound");
        }
        templateDefinitionMap.put(templateName, templateDefinition);
        templateDefinitionSets.add(templateName);
    }

    @Override
    public TemplateDefinition getTemplateDefinition(String templateName) {
        return templateDefinitionMap.get(templateName);
    }

    public void removeTemplateDefinition(String templateName) {
        templateDefinitionMap.remove(templateName);
        templateDefinitionSets.remove(templateName);
    }

    public void removeMultipleTemplateDefinition(String templateName) {
        multipleTemplateDefinitionMap.remove(templateName);
        multipleTemplateDefinitionSets.remove(templateName);
    }

    @Override
    public void preInstantiateTemplates(){
        //初始化 模板
        for (String templateName : templateDefinitionSets) {
            getTemplate(templateName);
        }
        //初始化组合模板
        for (String templateName : multipleTemplateDefinitionSets) {
            getMultipleTemplate(templateName);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerMultipleTemplateDefinition(String multipleTemplateDefinitionName, MultipleTemplateDefinition multipleTemplateDefinition) {
        MultipleTemplateDefinition multipleTemplateDefinitionTemp = multipleTemplateDefinitionMap.get(multipleTemplateDefinitionName);
        if (null != multipleTemplateDefinitionTemp&&!getAllowTemplateDefinitionOverriding()) {
            throw new IllegalStateException("Could not register MultipleTemplateDefinition [" + multipleTemplateDefinition +
                    "] under MultipleTemplateDefinition name '" + multipleTemplateDefinitionName + "': there is already object [" + multipleTemplateDefinitionTemp + "] bound");
        }
        multipleTemplateDefinitionMap.put(multipleTemplateDefinitionName, multipleTemplateDefinition);
        multipleTemplateDefinitionSets.add(multipleTemplateDefinitionName);
    }

    @Override
    public MultipleTemplateDefinition getMultipleTemplateDefinition(String multipleTemplateDefinitionName) {
        return multipleTemplateDefinitionMap.get(multipleTemplateDefinitionName);
    }

    @Override
    public Set<String> getTemplateNames() {
        return templateNameSets;
    }

    /**
     * 获取组合模板名集合
     *
     * @return
     */
    @Override
    public Set<String> getMultipleTemplateNames() {
        return multipleTemplateNameSets;
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public void destroyTemplates() {
        super.destroyTemplates();
    }

    public Boolean getAllowTemplateDefinitionOverriding() {
        return allowTemplateDefinitionOverriding;
    }

    public void setAllowTemplateDefinitionOverriding(Boolean allowTemplateDefinitionOverriding) {
        this.allowTemplateDefinitionOverriding = allowTemplateDefinitionOverriding;
    }
}
