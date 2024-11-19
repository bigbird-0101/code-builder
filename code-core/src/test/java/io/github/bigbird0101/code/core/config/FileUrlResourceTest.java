package io.github.bigbird0101.code.core.config;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-06 16:59:31
 */
class FileUrlResourceTest {
    @Test
    public void test() throws IOException {
        URL resource = ResourceUtil.getResource("META-INF/templates/testCodeNodeXml.template");
        FileUrlResource fileUrlResource=new FileUrlResource(resource);
        assertNotNull(IoUtil.readUtf8(fileUrlResource.getInputStream()));
    }
}