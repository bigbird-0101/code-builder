package io.github.bigbird0101.code.core.template.targetfile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OnlySubFourTargetFilePrefixNameStrategyTest {

    private OnlySubFourTargetFilePrefixNameStrategy onlySubFourTargetFilePrefixNameStrategyUnderTest;

    @BeforeEach
    void setUp() {
        onlySubFourTargetFilePrefixNameStrategyUnderTest = new OnlySubFourTargetFilePrefixNameStrategy();
    }

    @Test
    void testGetTypeValue() {
        assertEquals(2, onlySubFourTargetFilePrefixNameStrategyUnderTest.getTypeValue());
    }

    @Test
    void testPrefixStrategy() {
        assertEquals("Ource",
                onlySubFourTargetFilePrefixNameStrategyUnderTest.prefixStrategy(null, "srcSource", new HashMap<>()));
    }
}
