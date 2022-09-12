package com.fpp.code.core.template;

import com.fpp.code.core.common.ReflectionUtils;
import org.junit.jupiter.api.Test;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-12 20:35:37
 */
public class ToolTemplateResolverTest {

    @Test
    public void testFunction() {
        final Object o = ReflectionUtils.invokeEnumMethod(ToolTemplateResolver.class, "Function",
                "sub", "done", "TabTest,9,10");
        System.out.println(o);
    }
}