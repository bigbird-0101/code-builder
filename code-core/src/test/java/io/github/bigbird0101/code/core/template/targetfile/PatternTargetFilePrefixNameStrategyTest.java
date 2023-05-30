package io.github.bigbird0101.code.core.template.targetfile;

import io.github.bigbird0101.code.core.template.AbstractTemplate;
import io.github.bigbird0101.code.core.template.TemplateResolver;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PatternTargetFilePrefixNameStrategyTest {
    @Test
    public void testGetTypeValue() {
        PatternTargetFilePrefixNameStrategy strategy = new PatternTargetFilePrefixNameStrategy();
        assertEquals(3, strategy.getTypeValue());
    }

    @Test
    public void testSetPattern() {
        PatternTargetFilePrefixNameStrategy strategy = new PatternTargetFilePrefixNameStrategy();
        String pattern = "test";
        strategy.setPattern(pattern);
        assertEquals(pattern, strategy.getPattern());
    }

    @Test
    public void testPrefixStrategy() {
        AbstractTemplate abstractTemplate = mock(AbstractTemplate.class);
        TemplateResolver templateResolver = mock(TemplateResolver.class);
        when(abstractTemplate.getTemplateResolver()).thenReturn(templateResolver);
        Map<String, Object> dataModel = mock(Map.class);
        when(templateResolver.resolver(anyString(), eq(dataModel))).thenReturn("test");
        PatternTargetFilePrefixNameStrategy strategy = new PatternTargetFilePrefixNameStrategy();
        strategy.setPattern("test");
        assertEquals("test", strategy.prefixStrategy(abstractTemplate, "srcSource", dataModel));
    }
}
