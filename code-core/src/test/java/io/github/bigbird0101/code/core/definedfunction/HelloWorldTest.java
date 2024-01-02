package io.github.bigbird0101.code.core.definedfunction;

import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
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
        final Template dao = genericTemplateContext.getTemplate("hello_world");
        Map<String, Object> dataModel = new HashMap<>(10);
        dataModel.put("helloWorld","hello world");
        final String templateResult = dao.process(dataModel);
        Assertions.assertNotNull(templateResult);
    }
}
