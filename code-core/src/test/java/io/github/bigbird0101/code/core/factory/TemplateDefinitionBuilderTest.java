package io.github.bigbird0101.code.core.factory;

import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.factory.TemplateDefinitionBuilder;
import io.github.bigbird0101.code.core.template.DefaultNoHandleFunctionTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-27 22:47:24
 */
class TemplateDefinitionBuilderTest {

    @Test
    void build() {
        final TemplateDefinition build = TemplateDefinitionBuilder.build(DefaultNoHandleFunctionTemplate.class);
        Assertions.assertNotNull(build);
    }
}