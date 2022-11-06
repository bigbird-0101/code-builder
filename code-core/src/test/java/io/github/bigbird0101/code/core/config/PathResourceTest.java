package io.github.bigbird0101.code.core.config;

import cn.hutool.core.io.IoUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-06 16:59:19
 */
class PathResourceTest {
     @Test
     public void test() throws IOException {
         PathResource pathResource=new PathResource(Paths.get("C:\\Users\\Administrator\\IdeaProjects\\code-builder" +
                 "\\code-core\\src\\test\\resources\\testCodeNodeXml.template"));
         assertNotNull(IoUtil.readUtf8(pathResource.getInputStream()));
         final Resource relative = pathResource.createRelative("C:\\Users\\Administrator\\IdeaProjects\\code-builder" +
                 "\\code-core\\src\\test\\resources\\testCodeNodeXml.template");
         assertNotNull(relative);
     }
}