package io.github.bigbird0101.code.core.template.resolver;

import io.github.bigbird0101.code.core.template.DefaultTemplateResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author pengpeng_fu@infinova.com.cn
 * @date 2024-01-02 14:15
 */
public class ToolTemplateLangResolverTest {
    private ToolTemplateLangResolver toolTemplateLangResolverTest;

    @BeforeEach
    void setUp() {
        toolTemplateLangResolverTest = new ToolTemplateLangResolver();
        toolTemplateLangResolverTest.setTemplateResolver(new DefaultTemplateResolver());
    }
    @Test
    void testRep() {
        // Setup
        final Map<String, Object> dataModal = new HashMap<>();

        // Run the test
        final String result = toolTemplateLangResolverTest.langResolver("*{tool.rep(abcd,abcd,abcde)}*", dataModal);

        // Verify the results
        assertEquals("abcde", result);
    }

    @Test
    void testRepVar() {
        // Setup
        final Map<String, Object> dataModal = new HashMap<>();
        dataModal.put("abcd","123abcd");
        dataModal.put("abcde","abcd");
        // Run the test
        final String result = toolTemplateLangResolverTest.langResolver("*{tool.rep(*{abcd}*,*{abcde}*,abcde)}*", dataModal);

        // Verify the results
        assertEquals("123abcde", result);
    }
}
