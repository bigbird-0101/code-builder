package io.github.bigbird0101.code.core.template.targetfile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateFilePrefixNameStrategyFactoryTest {
@Test
public void testGetTemplateFilePrefixNameStrategy() {
    TemplateFilePrefixNameStrategyFactory factory = new TemplateFilePrefixNameStrategyFactory();
     // Positive Test Case
    TargetFilePrefixNameStrategy strategy1 = factory.getTemplateFilePrefixNameStrategy(1);
    assertTrue(strategy1 instanceof DefaultTargetFilePrefixNameStrategy);
     TargetFilePrefixNameStrategy strategy2 = factory.getTemplateFilePrefixNameStrategy(2);
    assertTrue(strategy2 instanceof OnlySubFourTargetFilePrefixNameStrategy);
     TargetFilePrefixNameStrategy strategy3 = factory.getTemplateFilePrefixNameStrategy(3);
    assertTrue(strategy3 instanceof PatternTargetFilePrefixNameStrategy);
     // Negative Test Case
    TargetFilePrefixNameStrategy strategy4 = factory.getTemplateFilePrefixNameStrategy(4);
    assertNull(strategy4);
}
}