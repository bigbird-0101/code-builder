package io.github.bigbird0101.code.core.config;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-06 16:59:19
 */
class PathResourceTest2 {
     @Test
     public void test() throws IOException {
         String path = ResourceUtil.getResource("template/testCodeNodeXml.template").getPath();
         String file = path.substring(1);
         PathResource pathResource=new PathResource(Paths.get(file));
         assertNotNull(IoUtil.readUtf8(pathResource.getInputStream()));
         final Resource relative = pathResource.createRelative(file);
         assertNotNull(relative);
     }
}