package io.github.bigbird0101.code.core.config;

import cn.hutool.core.io.IoUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-06 16:59:31
 */
class FileUrlResourceTest {
    @Test
    public void test() throws IOException {
        FileUrlResource fileUrlResource=new FileUrlResource("C:\\Users\\Administrator\\IdeaProjects\\code-builder" +
                "\\code-core\\src\\test\\resources\\testCodeNodeXml.template");
        assertNotNull(IoUtil.readUtf8(fileUrlResource.getInputStream()));
    }
}