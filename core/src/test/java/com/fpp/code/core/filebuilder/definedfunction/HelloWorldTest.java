package com.fpp.code.core.filebuilder.definedfunction;

import com.fpp.code.core.common.DbUtil;
import com.fpp.code.core.config.StandardEnvironment;
import com.fpp.code.core.context.GenericTemplateContext;
import com.fpp.code.core.domain.DataSourceConfig;
import com.fpp.code.core.domain.TableInfo;
import com.fpp.code.core.template.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {
    @Test
    public void helloWorld() throws SQLException {
        StandardEnvironment environment=new StandardEnvironment();
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        final Template dao = genericTemplateContext.getTemplate("Dao");
        Map<String, Object> temp = new HashMap<>(10);
        TableInfo tableInfo = DbUtil.getTableInfo(DataSourceConfig.getDataSourceConfig(genericTemplateContext
                .getEnvironment()), "tab_test", genericTemplateContext.getEnvironment());
        temp.put("tableInfo", tableInfo);
        dao.getTemplateVariables().putAll(temp);
        final String templateResult = dao.getTemplateResult();
        Assertions.assertNotNull(templateResult);
    }
}
