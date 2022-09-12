package com.fpp.code.core.filebuilder.definedfunction;

import com.fpp.code.core.common.DbUtil;
import com.fpp.code.core.config.JFramePageEnvironment;
import com.fpp.code.core.context.GenericTemplateContext;
import com.fpp.code.core.domain.DataSourceConfig;
import com.fpp.code.core.template.TableInfo;
import com.fpp.code.core.template.Template;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {
    @Test
    public void helloWorld() throws SQLException {
        JFramePageEnvironment environment=new JFramePageEnvironment();
        String userHome = System.getProperty("user.home");
        String projectHome=userHome+"\\Desktop\\tool\\";
        environment.setCoreConfigPath(projectHome+"codebuilder\\conf\\code.properties");
        environment.setTemplateConfigPath(projectHome+"codebuilder\\conf\\templates.json");
        environment.setTemplatesPath(projectHome+"codebuilder\\data\\templates");
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        final Template dao = genericTemplateContext.getTemplate("DaoCopy");
        Map<String, Object> temp = new HashMap<>(10);
        TableInfo tableInfo = DbUtil.getTableInfo(DataSourceConfig.getDataSourceConfig(genericTemplateContext.getEnvironment()), "tab_test", genericTemplateContext.getEnvironment());
        temp.put("tableInfo", tableInfo);
        dao.getTemplateVariables().putAll(temp);
        final String templateResult = dao.getTemplateResult();
        System.out.println(templateResult);
    }
}
