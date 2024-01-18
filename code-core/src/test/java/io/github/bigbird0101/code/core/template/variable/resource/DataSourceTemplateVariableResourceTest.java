package io.github.bigbird0101.code.core.template.variable.resource;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import io.github.bigbird0101.code.core.base.RandomUtils;
import io.github.bigbird0101.code.core.common.DbUtil;
import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.domain.TableInfo;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-25 16:31:38
 */
class DataSourceTemplateVariableResourceTest {

    @Test
    void getTemplateVariable() {
        try(MockedStatic<DbUtil> dbUtilMockedStatic = Mockito.mockStatic(DbUtil.class)) {
            TableInfo tableInfo = RandomUtils.randomPojo(TableInfo.class);
            dbUtilMockedStatic.when(()->DbUtil.getTableInfo(Mockito.any(),Mockito.anyString(),Mockito.any()))
                    .thenReturn(tableInfo);
            StandardEnvironment environment = new StandardEnvironment();
            environment.parse();
            DataSourceTemplateVariableResource dataSourceTemplateVariableResource =
                    new DataSourceTemplateVariableResource("tab_test", environment);
            final Map<String, Object> templateVariable = dataSourceTemplateVariableResource.getTemplateVariable();
            assertEquals(1, templateVariable.size());
            assertNotNull(templateVariable.get("tableInfo"));
        }
    }

    @Test
    public void testB(){
        final Resource resourceObj = ResourceUtil.getResourceObj("META-INF/code.properties");
        System.out.println(resourceObj);
    }
}