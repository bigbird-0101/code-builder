package com.fpp.code.core.template.variable.resource;

import com.fpp.code.core.config.StandardEnvironment;
import com.fpp.code.core.context.GenericTemplateContext;
import com.fpp.code.core.factory.TemplateDefinitionBuilder;
import com.fpp.code.core.template.DefaultNoHandleFunctionTemplate;
import com.fpp.code.core.template.Template;
import com.fpp.code.exception.TemplateResolveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-25 16:31:19
 */
class ConfigFileTemplateVariableResourceTest {
    ConfigFileTemplateVariableResource configFileTemplateVariableResource;
    @BeforeEach
    public void before(){
        configFileTemplateVariableResource=new ConfigFileTemplateVariableResource(
                ConfigFileTemplateVariableResourceTest.class.getClassLoader()
                        .getResourceAsStream("testTemplateVariableConfigFile.properties"));
    }


    @Test
    void getTemplateVariable() {
        final Map<String, Object> templateVariable = configFileTemplateVariableResource.getTemplateVariable();
        assertEquals(3, templateVariable.size());
    }

    @Test
    void getNoShareVar() {
        final Queue<Map<String, Object>> noShareVar = configFileTemplateVariableResource.getNoShareVar();
        assertEquals(2, noShareVar.size());
        assertEquals(2, Objects.requireNonNull(noShareVar.poll()).size());
    }

    @Test
    void testTemplate(){
        StandardEnvironment standardEnvironment=new StandardEnvironment();
        GenericTemplateContext genericTemplateContext=new GenericTemplateContext(standardEnvironment);
        final Template dao = genericTemplateContext.getTemplate("Dao");
        dao.getTemplateVariables().putAll(configFileTemplateVariableResource.getTemplateVariable());
        Assertions.assertThrows(TemplateResolveException.class, dao::getTemplateResult);
    }

    @Test
    void testSuccessTemplate(){
        StandardEnvironment standardEnvironment=new StandardEnvironment();
        GenericTemplateContext genericTemplateContext=new GenericTemplateContext(standardEnvironment);
        final String testConfigTemplateResource = "testConfigTemplateResource";
        genericTemplateContext.registerTemplateDefinition(testConfigTemplateResource, TemplateDefinitionBuilder.build(
                DefaultNoHandleFunctionTemplate.class,"testTemplateVariable.template"));
        final Template dao = genericTemplateContext.getTemplate("testConfigTemplateResource");
        final Queue<Map<String, Object>> noShareVar = configFileTemplateVariableResource.getNoShareVar();
        while(!noShareVar.isEmpty()){
            dao.getTemplateVariables().putAll(configFileTemplateVariableResource.getTemplateVariable());
            Optional.ofNullable(noShareVar.poll()).ifPresent(s->{
                dao.getTemplateVariables().putAll(s);
            });
            final String templateResult = dao.getTemplateResult();
            Assertions.assertNotNull(templateResult);
        }
    }
}