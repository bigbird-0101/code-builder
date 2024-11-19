package io.github.bigbird0101.code.core;

import cn.hutool.core.io.resource.ResourceUtil;

public class ExceptResultProvider {
    public static String getExceptResult(String templateName) {
        String expectedResult = ResourceUtil.readUtf8Str("META-INF/templates/exceptResult/exceptResult-" + templateName + ".txt");
        return expectedResult.replaceAll("\\r\\n", "\n");
    }
}
