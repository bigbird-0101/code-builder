package io.github.bigbird0101.code.core.share;

import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.template.MultipleTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bigbird-0101
 * @date 2024-06-12 20:22
 */
public class MultipleTemplateDefinitionWrapper extends AbstractTemplateContextProvider {
    private final String multipleTemplateName;
    private final MultipleTemplateDefinition multipleTemplateDefinition;
    private final List<TemplateDefinitionWrapper> templateDefinitionWrapperList;


    public MultipleTemplateDefinitionWrapper(String multipleTemplateName, MultipleTemplateDefinition
            multipleTemplateDefinition, List<TemplateDefinitionWrapper> templateDefinitionWrapperList) {
        this.multipleTemplateName = multipleTemplateName;
        this.multipleTemplateDefinition = multipleTemplateDefinition;
        this.templateDefinitionWrapperList = templateDefinitionWrapperList;
    }

    public String getMultipleTemplateName() {
        return multipleTemplateName;
    }

    public MultipleTemplateDefinition getMultipleTemplateDefinition() {
        return multipleTemplateDefinition;
    }

    public List<TemplateDefinitionWrapper> getTemplateDefinitionWrapperList() {
        return templateDefinitionWrapperList;
    }

    public void registerAndRefreshMultipleTemplate() {
        List<TemplateDefinitionWrapper> sortWrapper = new ArrayList<>();
        for (TemplateDefinitionWrapper templateDefinitionWrapper : getTemplateDefinitionWrapperList()) {
            templateDefinitionWrapper.sortGetDependTemplateDefinitionList(templateDefinitionWrapper, sortWrapper);
        }
        GenericTemplateContext templateContext = (GenericTemplateContext) getTemplateContext();
        DefaultListableTemplateFactory defaultListableTemplateFactory = templateContext.getTemplateFactory();
        TemplateDefinitionWrapper.doDealSortedTemplateWrapper(sortWrapper, templateContext);
        templateContext.registerMultipleTemplateDefinition(getMultipleTemplateName(), getMultipleTemplateDefinition());
        defaultListableTemplateFactory.preInstantiateTemplates();
        final MultipleTemplate multipleTemplate = templateContext.getMultipleTemplate(getMultipleTemplateName());
        defaultListableTemplateFactory.refreshMultipleTemplate(multipleTemplate);
        multipleTemplate.getTemplates().forEach(defaultListableTemplateFactory::refreshTemplate);
    }
}