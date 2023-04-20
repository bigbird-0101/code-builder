package io.github.bigbird0101.code.core.factory;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.URLUtil;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.template.DefaultNoHandleFunctionTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-27 22:47:24
 */
class TemplateDefinitionBuilderTest2 {

    @Test
    void build() {
        final TemplateDefinition build = TemplateDefinitionBuilder.build(DefaultNoHandleFunctionTemplate.class);
        Assertions.assertNotNull(build);
    }

    @Test
    void testBuild() {
        final TemplateDefinition build = TemplateDefinitionBuilder.build(
                new File(URLUtil.decode(ResourceUtil.getResourceObj("template/testCodeNodeXml.template").getUrl().getFile()))
        );
        Assertions.assertNotNull(build);
    }
}