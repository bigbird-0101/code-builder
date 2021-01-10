package com.fpp.code.core.factory;

import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.factory.config.MultipleTemplateDefinition;
import com.fpp.code.core.factory.config.MultipleTemplateDefinitionRegistry;
import com.fpp.code.core.factory.config.TemplateDefinition;
import com.fpp.code.core.factory.config.TemplateDefinitionRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 默认的模板工厂实现
 * @author Administrator
 */
public class DefaultListableTemplateFactory extends AbstractOperateTemplateTemplateFactory implements TemplateDefinitionRegistry,ConfigurableListableTemplateFactory, MultipleTemplateDefinitionRegistry {

    private Map<String, TemplateDefinition> templateDefinitionMap=new HashMap<>();
    private Set<String> templateDefinitionSets=new HashSet<>();

    private Map<String, MultipleTemplateDefinition> multipleTemplateDefinitionMap=new HashMap<>();
    private Set<String> multipleTemplateDefinitionSets=new HashSet<>();

    private Environment environment;

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
    public void preInstantiateTemplates() throws CodeConfigException {
        //初始化 模板
        for (String templateName:templateDefinitionSets){
            getTemplate(templateName);
        }
        //初始化组合模板
        for (String templateName:multipleTemplateDefinitionSets){
            getMultipleTemplate(templateName);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
         this.environment=environment;
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

    @Override
    public Set<String> getTemplateNames(){
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
}
