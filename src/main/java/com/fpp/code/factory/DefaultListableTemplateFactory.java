package com.fpp.code.factory;

import com.fpp.code.factory.config.MultipleTemplateDefinition;
import com.fpp.code.factory.config.MultipleTemplateDefinitionRegistry;
import com.fpp.code.factory.config.TemplateDefinition;
import com.fpp.code.factory.config.TemplateDefinitionRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultListableTemplateFactory extends AbstractOperateTemplateTemplateFactory implements TemplateDefinitionRegistry,ConfigurableListableTemplateFactory, MultipleTemplateDefinitionRegistry {

    private Map<String, TemplateDefinition> templateDefinitionMap=new HashMap<>();
    private Set<String> templateDefinitionSets=new HashSet<>();

    private Map<String, MultipleTemplateDefinition> multipleTemplateDefinitionMap=new HashMap<>();
    private Set<String> multipleTemplateDefinitionSets=new HashSet<>();

    @Override
    public void registerTemplateDefinition(String templateName, TemplateDefinition templateDefinition) {
        TemplateDefinition templateDefinitionTemp = templateDefinitionMap.get(templateName);
        if(null!=templateDefinitionTemp){
            throw new IllegalStateException("Could not register TemplateDefinition [" + templateDefinition +
                    "] under TemplateDefinition name '" + templateName + "': there is already object [" + templateDefinitionTemp + "] bound");
        }
        templateDefinitionMap.put(templateName,templateDefinition);
        templateDefinitionSets.add(templateName);
    }

    @Override
    public TemplateDefinition getTemplateDefinition(String templateName) {
        return templateDefinitionMap.get(templateName);
    }

    public void removeTemplateDefinition(String templateName){
        templateDefinitionMap.remove(templateName);
        templateDefinitionSets.remove(templateName);
    }

    @Override
    public void preInstantiateTemplates() {

    }

    @Override
    public void registerMultipleTemplateDefinition(String multipleTemplateDefinitionName, MultipleTemplateDefinition multipleTemplateDefinition) {
        MultipleTemplateDefinition multipleTemplateDefinitionTemp = multipleTemplateDefinitionMap.get(multipleTemplateDefinitionName);
        if(null!=multipleTemplateDefinitionTemp){
            throw new IllegalStateException("Could not register MultipleTemplateDefinition [" + multipleTemplateDefinition +
                    "] under MultipleTemplateDefinition name '" + multipleTemplateDefinitionName + "': there is already object [" + multipleTemplateDefinitionTemp + "] bound");
        }
        multipleTemplateDefinitionMap.put(multipleTemplateDefinitionName,multipleTemplateDefinition);
        multipleTemplateDefinitionSets.add(multipleTemplateDefinitionName);
    }

    @Override
    public MultipleTemplateDefinition getMultipleTemplateDefinition(String multipleTemplateDefinitionName) {
        return multipleTemplateDefinitionMap.get(multipleTemplateDefinitionName);
    }
}
