package io.github.bigbird0101.code.core.filebuilder;

import io.github.bigbird0101.code.core.DataModelProvider;
import io.github.bigbird0101.code.core.domain.TableInfo;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.core.template.targetfile.DefaultTargetFilePrefixNameStrategy;
import io.github.bigbird0101.code.core.template.targetfile.TargetFilePrefixNameStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.github.bigbird0101.code.core.template.variable.resource.TemplateVariableResource.DEFAULT_SRC_RESOURCE_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultFileNameBuilderImplTest {

    private DefaultFileNameBuilderImpl fileNameBuilder;
    private TableInfo tableInfo;
    private Map<String, Object> dataModel;

    @BeforeEach
    void setUp() {
        fileNameBuilder = new DefaultFileNameBuilderImpl();
        tableInfo = DataModelProvider.getTableInfo();
        dataModel = DataModelProvider.getDataModel();
    }

    @Test
    void nameBuilder_WithValidTableInfoAndTemplate_ReturnsCorrectFileName() {
        // Given
        tableInfo.setTableName("users");
        dataModel.put("tableInfo", tableInfo);

        Template template = mock(Template.class);
        when(template.getTargetFileSuffixName()).thenReturn("java");
        when(template.getSrcPackage()).thenReturn("java");

        TargetFilePrefixNameStrategy prefixNameStrategy = new DefaultTargetFilePrefixNameStrategy();
        when(template.getTargetFilePrefixNameStrategy()).thenReturn(prefixNameStrategy);

        // When
        String result = fileNameBuilder.nameBuilder(template, dataModel);

        // Then
        assertEquals("SJava.java", result);
        verify(template).getTargetFileSuffixName();
        verify(template).getTargetFilePrefixNameStrategy();
    }

    @Test
    void nameBuilder_WithoutTableInfoButWithDefaultResourceKey_ReturnsCorrectFileName() {
        // Given
        dataModel.put(DEFAULT_SRC_RESOURCE_KEY, "defaultTable");

        Template template = mock(Template.class);
        when(template.getTargetFileSuffixName()).thenReturn("java");
        when(template.getSrcPackage()).thenReturn("java");

        TargetFilePrefixNameStrategy prefixNameStrategy = new DefaultTargetFilePrefixNameStrategy();
        when(template.getTargetFilePrefixNameStrategy()).thenReturn(prefixNameStrategy);

        // When
        String result = fileNameBuilder.nameBuilder(template, dataModel);

        // Then
        assertEquals("SJava.java", result);
        verify(template).getTargetFileSuffixName();
        verify(template).getTargetFilePrefixNameStrategy();
    }


    @Test
    void nameBuilder_WithEmptyTableInfo_ReturnsDefaultResourceValue() {
        // Given
        dataModel.put(DEFAULT_SRC_RESOURCE_KEY, "defaultTable");

        Template template = mock(Template.class);
        when(template.getTargetFileSuffixName()).thenReturn("java");
        when(template.getSrcPackage()).thenReturn("java");

        TargetFilePrefixNameStrategy prefixNameStrategy = new DefaultTargetFilePrefixNameStrategy();
        when(template.getTargetFilePrefixNameStrategy()).thenReturn(prefixNameStrategy);

        // When
        String result = fileNameBuilder.nameBuilder(template, dataModel);

        // Then
        assertEquals("SJava.java", result);
        verify(template).getTargetFileSuffixName();
        verify(template).getTargetFilePrefixNameStrategy();
    }

    @Test
    void nameBuilder_WithNullTableInfoAndNoDefaultResourceKey_ReturnsEmptyString() {
        // Given
        Template template = mock(Template.class);
        when(template.getTargetFileSuffixName()).thenReturn("java");
        when(template.getSrcPackage()).thenReturn("java");

        TargetFilePrefixNameStrategy prefixNameStrategy = new DefaultTargetFilePrefixNameStrategy();
        when(template.getTargetFilePrefixNameStrategy()).thenReturn(prefixNameStrategy);

        // When
        String result = fileNameBuilder.nameBuilder(template, dataModel);

        // Then
        assertEquals("SJava.java", result);
        verify(template).getTargetFileSuffixName();
        verify(template).getTargetFilePrefixNameStrategy();
    }
}
