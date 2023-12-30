package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.template.resolver.SimpleTemplateResolver;
import org.junit.jupiter.api.Test;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 22:05:19
 */
class AbstractTemplateResolverTest {

    @Test
    void replaceVariable() {
        SimpleTemplateResolver simpleTemplateResolver=new SimpleTemplateResolver();
        final String s = simpleTemplateResolver.replaceVariable("这是上层循环*{column.name}*", "column.name", "column2.name");
        System.out.println(s);
    }

    @Test
    void replaceVariable2() {
        SimpleTemplateResolver simpleTemplateResolver=new SimpleTemplateResolver();
        final String s = simpleTemplateResolver.replaceFirstVariable("这是上层循环*{column.name}*", "column.name", "column2");
        System.out.println(s);
    }
}