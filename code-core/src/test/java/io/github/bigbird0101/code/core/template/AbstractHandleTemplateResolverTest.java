package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AbstractHandleTemplateResolverTest {

    @Mock
    private Environment mockEnvironment;

    private AbstractHandleTemplateResolver abstractHandleTemplateResolverUnderTest;

    @BeforeEach
    void setUp() {
        abstractHandleTemplateResolverUnderTest = new AbstractHandleTemplateResolver(mockEnvironment) {
            @Override
            public String resolver(String srcData, Map<String, Object> replaceKeyValue) throws TemplateResolveException {
                return null;
            }
        };
    }

    @Test
    void testGetSuffix() {
        assertEquals("", abstractHandleTemplateResolverUnderTest.getSuffix("templateContent"));
    }

    @Test
    void testGetPrefix() {
        assertEquals("", abstractHandleTemplateResolverUnderTest.getPrefix("templateContent"));
    }

    @Test
    void testGetNoResolverFunctionName() {
        assertEquals("srcFunctionName",
                abstractHandleTemplateResolverUnderTest.getNoResolverFunctionName("srcFunctionName"));
    }

    @Test
    void testGetFunctionS() {
        // Setup
        final Map<String, String> expectedResult = new HashMap<>();

        // Run the test
        final Map<String, String> result = abstractHandleTemplateResolverUnderTest.getFunctionS("templateContent");

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetTemplateFunctionName() {
        assertEquals("", abstractHandleTemplateResolverUnderTest.getTemplateFunctionName("src"));
    }
}
