package io.github.bigbird0101.code.core.template.targetfile;

import io.github.bigbird0101.code.core.template.DefaultHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.Template;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultTargetFilePrefixNameStrategyTest {
    @Test
    void testPrefixStrategy() {
        TargetFilePrefixNameStrategy strategy = new DefaultTargetFilePrefixNameStrategy();
        Template template = new DefaultHandleFunctionTemplate();
        template.setSrcPackage("src/main/java");
        Map<String, Object> dataModel = new HashMap<>();
        String srcSource = "src_source_table_name";
        String expected = "NameJava";
        String actual = strategy.prefixStrategy(template, srcSource, dataModel);
        assertEquals(expected, actual);
    }
}