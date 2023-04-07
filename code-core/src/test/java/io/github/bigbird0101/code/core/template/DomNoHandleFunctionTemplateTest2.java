package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.factory.TemplateDefinitionBuilder;
import io.github.bigbird0101.code.core.template.languagenode.DomScriptCodeNodeBuilderTest2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 20:32:10
 */
class DomNoHandleFunctionTemplateTest2 {

 @Test
 void test() throws SQLException {
   StandardEnvironment environment=new StandardEnvironment();
   GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
   final String testConfigTemplateResource = "testCodeNodeXml";
   genericTemplateContext.registerTemplateDefinition(testConfigTemplateResource, TemplateDefinitionBuilder.build(
           DomNoHandleFunctionTemplate.class, "template/testCodeNodeXml.template"));
   final Template dao = genericTemplateContext.getTemplate("testCodeNodeXml");
     Map<String, Object> dataModel = new HashMap<>(DomScriptCodeNodeBuilderTest2.doBuildData(environment));
   final String templateResult = dao.process(dataModel);
   System.out.println(templateResult);
   Assertions.assertNotNull(templateResult);
 }
}