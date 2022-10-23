package io.github.bigbird0101.code.core.definedfunction;

import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.factory.TemplateDefinitionBuilder;
import io.github.bigbird0101.code.core.template.DefaultNoHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.Template;
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
