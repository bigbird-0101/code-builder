package com.fpp.code.core.filebuilder.definedfunction;

import com.fpp.code.core.config.StandardEnvironment;
import com.fpp.code.core.context.GenericTemplateContext;
import com.fpp.code.core.factory.TemplateDefinitionBuilder;
import com.fpp.code.core.template.DefaultNoHandleFunctionTemplate;
import com.fpp.code.core.template.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {
    @Test
    public void helloWorld() {
        StandardEnvironment environment=new StandardEnvironment();
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        final String testConfigTemplateResource = "hello_world";
        genericTemplateContext.registerTemplateDefinition(testConfigTemplateResource, TemplateDefinitionBuilder.build(
                DefaultNoHandleFunctionTemplate.class,"hello_world.template"));
        final Template dao = genericTemplateContext.getTemplate("hello_world");
        Map<String, Object> temp = new HashMap<>(10);
        temp.put("helloWorld","hello world");
        dao.getTemplateVariables().putAll(temp);
        final String templateResult = dao.getTemplateResult();
        Assertions.assertNotNull(templateResult);
    }
}
