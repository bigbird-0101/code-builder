package com.fpp.code.core.template;

import com.fpp.code.core.config.StandardEnvironment;
import com.fpp.code.core.context.GenericTemplateContext;
import com.fpp.code.core.factory.TemplateDefinitionBuilder;
import com.fpp.code.core.template.languagenode.DomScriptCodeNodeBuilderTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 17:37:11
 */
class DomHandleFunctionTemplateTest {

    @Test
    void test() throws SQLException {
        StandardEnvironment environment=new StandardEnvironment();
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        final String testConfigTemplateResource = "testCodeNodeXml";
        genericTemplateContext.registerTemplateDefinition(testConfigTemplateResource, TemplateDefinitionBuilder.build(
                DomHandleFunctionTemplate.class,"testCodeNodeXml.template"));
        final Template dao = genericTemplateContext.getTemplate("testCodeNodeXml");
        dao.getTemplateVariables().putAll(DomScriptCodeNodeBuilderTest.doBuildData(environment));
        final String templateResult = dao.getTemplateResult();
        System.out.println(templateResult);
        Assertions.assertNotNull(templateResult);
    }
}