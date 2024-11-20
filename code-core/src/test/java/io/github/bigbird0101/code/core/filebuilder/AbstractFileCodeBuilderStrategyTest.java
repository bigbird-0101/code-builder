package io.github.bigbird0101.code.core.filebuilder;

import cn.hutool.core.map.MapUtil;
import io.github.bigbird0101.code.core.config.CoreConfig;
import io.github.bigbird0101.code.core.domain.DefinedFunctionDomain;
import io.github.bigbird0101.code.core.domain.ProjectTemplateInfoConfig;
import io.github.bigbird0101.code.core.domain.TemplateFileClassInfo;
import io.github.bigbird0101.code.core.filebuilder.definedfunction.DefaultDefinedFunctionResolver;
import io.github.bigbird0101.code.core.template.AbstractHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AbstractFileCodeBuilderStrategyTest {

    private final Template template = Mockito.mock(AbstractHandleFunctionTemplate.class);
    @InjectMocks
    private AbstractFileCodeBuilderStrategy abstractFileCodeBuilderStrategyUnderTest = new AbstractFileCodeBuilderStrategy() {
        @Override
        public String doneCode(Map<String, Object> dataModel) throws TemplateResolveException {
            return "";
        }

        @Override
        public void fileWrite(String code, Map<String, Object> dataModel) {

        }
    };
    @InjectMocks
    private CoreConfig coreConfig;
    @Mock
    private ProjectTemplateInfoConfig projectTemplateInfoConfig;

    @BeforeEach
    void setUp() {
        abstractFileCodeBuilderStrategyUnderTest.setCoreConfig(coreConfig);
        abstractFileCodeBuilderStrategyUnderTest.setTemplate(template);
        abstractFileCodeBuilderStrategyUnderTest.setDefinedFunctionResolver(new DefaultDefinedFunctionResolver());
        Mockito.when(template.getTemplateName()).thenReturn("AbstractFileCodeBuilderStrategyTest");
        Mockito.when(projectTemplateInfoConfig.getTemplateSelectedGroup()).thenReturn(MapUtil.of("AbstractFileCodeBuilderStrategyTest", Arrays.asList("", "")));
        DefinedFunctionDomain definedFunctionDomain = new DefinedFunctionDomain("name,code",
                "templateFunctionName", "id");
        Mockito.when(projectTemplateInfoConfig.getDefinedFunctionDomainList()).thenReturn(Collections.singletonList(definedFunctionDomain));
    }

    @Test
    void testFilterFunction() {
        // Setup
        final TemplateFileClassInfo templateFileClassInfo = new TemplateFileClassInfo("templateClassPrefix",
                "templateClassSuffix", MapUtil.of("templateFunctionName", "templateFunction @ApiImplicitParam" +
                "(paramType = \"query\", name = \"id\", value = \"a\", required = true, dataType = \"Integer\"); @param id testid; " +
                " info (用户 {},id); Integer id;object.setId(id); public void  setId(Integer id){ this.id=id;}   "));
        final Map<String, Object> dataModel = new HashMap<>();

        // Run the test
        abstractFileCodeBuilderStrategyUnderTest.filterFunction(templateFileClassInfo, dataModel);

        // Verify the results
    }

    @Test
    @Disabled
    void testGetSrcFileCode() {
        assertEquals("", abstractFileCodeBuilderStrategyUnderTest.getSrcFileCode("srcFilePath"));
    }

    @Test
    @Disabled
    void testGetFilePath() {
        // Setup
        final Map<String, Object> dataModel = new HashMap<>();

        // Run the test
        final String result = abstractFileCodeBuilderStrategyUnderTest.getFilePath(dataModel);

        // Verify the results
        assertEquals("result", result);
    }

    @Test
    void testResolverStrategy() {
        // Setup
        final TemplateFileClassInfo templateFileClassInfo = new TemplateFileClassInfo("templateClassPrefix",
                "templateClassSuffix", new HashMap<>());
        final Map<String, Object> dataModel = new HashMap<>();

        // Run the test
        abstractFileCodeBuilderStrategyUnderTest.resolverStrategy(templateFileClassInfo, dataModel);

        // Verify the results
    }
}
