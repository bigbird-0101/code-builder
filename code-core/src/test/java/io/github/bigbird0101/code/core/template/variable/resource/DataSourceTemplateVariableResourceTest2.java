package io.github.bigbird0101.code.core.template.variable.resource;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.template.variable.resource.DataSourceTemplateVariableResource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-25 16:31:38
 */
class DataSourceTemplateVariableResourceTest2 {

    @Test
    @Disabled
    void getTemplateVariable() {
        StandardEnvironment environment=new StandardEnvironment();
        environment.parse();
        DataSourceTemplateVariableResource dataSourceTemplateVariableResource=
                new DataSourceTemplateVariableResource("tab_test",environment);
        final Map<String, Object> templateVariable = dataSourceTemplateVariableResource.getTemplateVariable();
        assertEquals(1,templateVariable.size());
        assertNotNull(templateVariable.get("tableInfo"));
    }

    @Test
    public void testB(){
        final Resource resourceObj = ResourceUtil.getResourceObj("META-INF/code.properties");
        System.out.println(resourceObj);
    }
}