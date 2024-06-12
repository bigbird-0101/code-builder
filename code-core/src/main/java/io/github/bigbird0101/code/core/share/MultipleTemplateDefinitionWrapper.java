package io.github.bigbird0101.code.core.share;

import io.github.bigbird0101.code.core.config.FileUrlResource;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.github.bigbird0101.code.core.config.AbstractEnvironment.putTemplateContent;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;

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
        for (TemplateDefinitionWrapper templateDefinitionWrapper : sortWrapper) {
            try {
                TemplateDefinition templateDefinition = templateDefinitionWrapper.getTemplateDefinition();
                FileUrlResource templateResource = (FileUrlResource) templateDefinition.getTemplateResource();
                File newFile = templateResource.getFile();
                putTemplateContent(newFile.getAbsolutePath(), IOUtils.toString(newInputStream(newFile.toPath()), UTF_8));
                String templateName = templateDefinitionWrapper.getTemplateName();
                templateContext.registerTemplateDefinition(templateName, templateDefinition);
                defaultListableTemplateFactory.preInstantiateTemplates();
                defaultListableTemplateFactory.refreshTemplate(templateContext.getTemplate(templateName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        templateContext.registerMultipleTemplateDefinition(getMultipleTemplateName(), getMultipleTemplateDefinition());
        defaultListableTemplateFactory.preInstantiateTemplates();
        final MultipleTemplate multipleTemplate = templateContext.getMultipleTemplate(getMultipleTemplateName());
        defaultListableTemplateFactory.refreshMultipleTemplate(multipleTemplate);
        multipleTemplate.getTemplates().forEach(defaultListableTemplateFactory::refreshTemplate);
    }
}