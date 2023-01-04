package io.github.bigbird0101.code.core.context;

import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;

/**
 * @author fpp
 * @version 1.0
 */
public class GenericTemplateContext extends AbstractRefreshTemplateContext {
    public GenericTemplateContext(Environment environment){
        super(environment);
        this.refresh();
    }

    public GenericTemplateContext(){
        this(new StandardEnvironment());
    }

    @Override
    public void registerMultipleTemplateDefinition(String multipleTemplateDefinitionName, MultipleTemplateDefinition multipleTemplateDefinition) {
        getTemplateFactory().registerMultipleTemplateDefinition(multipleTemplateDefinitionName,multipleTemplateDefinition);
    }

    @Override
    public MultipleTemplateDefinition getMultipleTemplateDefinition(String multipleTemplateDefinitionName) {
        return getTemplateFactory().getMultipleTemplateDefinition(multipleTemplateDefinitionName);
    }

    @Override
    public void registerTemplateDefinition(String templateName, TemplateDefinition templateDefinition) {
        getTemplateFactory().registerTemplateDefinition(templateName, templateDefinition);
    }

    @Override
    public TemplateDefinition getTemplateDefinition(String templateName) {
        return getTemplateFactory().getTemplateDefinition(templateName);
    }

}
