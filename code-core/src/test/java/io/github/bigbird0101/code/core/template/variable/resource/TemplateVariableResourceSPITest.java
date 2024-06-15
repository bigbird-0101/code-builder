package io.github.bigbird0101.code.core.template.variable.resource;

import cn.hutool.core.io.FileUtil;
import io.github.bigbird0101.code.core.spi.SPIServiceLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author bigbird-0101
 * @date 2024-01-18 22:10
 */
public class TemplateVariableResourceSPITest {
    @Test
    public void test(){
        String resourceAsStream = ConfigJsonFileTemplateVariableResourceTest.class.getClassLoader()
                .getResource("testTemplateVariableConfigFile.json").getFile();
        TemplateVariableResource templateVariableResource = SPIServiceLoader
                .loadService(TemplateVariableResource.class, FileUtil.getSuffix(resourceAsStream));
        TemplateVariableResource json = SPIServiceLoader.loadService(TemplateVariableResource.class, "json");
        Assertions.assertEquals(templateVariableResource,json);

        String file = ConfigFileTemplateVariableResourceTest.class.getClassLoader()
                .getResource("testTemplateVariableConfigFile.properties").getFile();
        TemplateVariableResource templateVariableResourceProperties = SPIServiceLoader
                .loadService(TemplateVariableResource.class, FileUtil.getSuffix(file));
        TemplateVariableResource properties = SPIServiceLoader.loadService(TemplateVariableResource.class, "properties");
        Assertions.assertEquals(templateVariableResourceProperties,properties);
    }
}
