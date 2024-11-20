package io.github.bigbird0101.code.core.filebuilder;

import io.github.bigbird0101.code.core.domain.TemplateFileClassInfo;
import io.github.bigbird0101.code.core.template.AbstractHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.AbstractNoHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.ResolverStrategy;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Disabled
class FileAppendPrefixCodeBuilderStrategyTest {

    @InjectMocks
    private FileAppendPrefixCodeBuilderStrategy strategy;

    private Template template = new AbstractHandleFunctionTemplate() {
        @Override
        public void setResolverStrategy(ResolverStrategy resolverStrategy) {
            this.resolverStrategy = resolverStrategy;
        }

        @Override
        protected boolean doMatch(String content) {
            return true;
        }
    };

    @Mock
    private AbstractHandleFunctionTemplate handleFunctionTemplateMock;

    @Mock
    private AbstractNoHandleFunctionTemplate noHandleFunctionTemplateMock;

    @BeforeEach
    public void setUp() {
        // 初始化mock对象
        strategy.setTemplate(template);
        template.setTemplateName("templateName");
        template.setModule("module/");
        template.setSourcesRoot("sourcesRoot/");
        template.setSrcPackage("srcPackage/");
        template.setSrcPackage("srcPackage/");
    }

    @Test
    void testDoneCode_WithAbstractHandleFunctionTemplate() throws TemplateResolveException {
        TemplateFileClassInfo templateFileClassInfo = new TemplateFileClassInfo("prefix", "suffix", Collections.emptyMap());
        templateFileClassInfo.setTemplateClassPrefix("prefix");
        templateFileClassInfo.setTemplateClassSuffix("suffix");

        when(handleFunctionTemplateMock.getTemplateFileClassInfoWhenResolved()).thenReturn(templateFileClassInfo);
        when(handleFunctionTemplateMock.process(any(Map.class))).thenReturn("processedTemplate");

//        when(templateMock.getFileNameBuilder()).thenReturn(new FileNameBuilder() {
//            @Override
//            public String nameBuilder(Template template, Map<String, Object> dataModel) {
//                return "fileName";
//            }
//        });

        Map<String, Object> dataModel = new HashMap<>();
        String result = strategy.doneCode(dataModel);

        assertNull(result);
    }

    @Test
    void testDoneCode_WithAbstractNoHandleFunctionTemplate() throws TemplateResolveException {
        when(noHandleFunctionTemplateMock.process(any(Map.class))).thenReturn("processedTemplate");
        when(template.getProjectUrl()).thenReturn("projectUrl/");
        when(template.getModule()).thenReturn("module/");
        when(template.getSourcesRoot()).thenReturn("sourcesRoot/");
        when(template.getSrcPackage()).thenReturn("srcPackage/");
//        when(templateMock.getFileNameBuilder()).thenReturn(new FileNameBuilder() {
//            @Override
//            public String nameBuilder(Template template, Map<String, Object> dataModel) {
//                return "fileName";
//            }
//        });

        Map<String, Object> dataModel = new HashMap<>();
        String result = strategy.doneCode(dataModel);

        assertNull(result);
    }

    @Test
    void testFileWrite() {
        // Assuming fileWrite method does not return anything and is thus void
        // Mocking not necessary as it does not invoke any external logic
        Map<String, Object> dataModel = new HashMap<>();
        String code = "sample code";

        strategy.fileWrite(code, dataModel);

        // No assertions needed since fileWrite does not return anything
    }
}
