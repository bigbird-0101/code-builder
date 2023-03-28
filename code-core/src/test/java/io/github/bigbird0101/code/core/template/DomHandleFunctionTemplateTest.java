package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.template.languagenode.DomScriptCodeNodeBuilderTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 17:37:11
 */
class DomHandleFunctionTemplateTest {

    @Test
    void test() throws SQLException {
        StandardEnvironment environment=new StandardEnvironment();
        environment.setTemplatesPath(Objects.requireNonNull(DomHandleFunctionTemplateTest.class.getResource("/")).toString());
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        final Template dao = genericTemplateContext.getTemplate("testCodeNodeXml");
        Map<String, Object> dataModel = new HashMap<>(DomScriptCodeNodeBuilderTest.doBuildData(environment));
        final String templateResult = dao.process(dataModel);
        System.out.println(templateResult);
        Assertions.assertNotNull(templateResult);
    }
}