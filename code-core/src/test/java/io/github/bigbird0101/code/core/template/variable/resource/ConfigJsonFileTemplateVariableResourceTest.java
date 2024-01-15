package io.github.bigbird0101.code.core.template.variable.resource;

import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.factory.TemplateDefinitionBuilder;
import io.github.bigbird0101.code.core.template.DefaultNoHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-25 16:31:19
 */
class ConfigJsonFileTemplateVariableResourceTest {
    ConfigJsonFileTemplateVariableResource configFileTemplateVariableResource;
    @BeforeEach
    public void before(){
        configFileTemplateVariableResource=new ConfigJsonFileTemplateVariableResource(
                ConfigJsonFileTemplateVariableResourceTest.class.getClassLoader()
                        .getResourceAsStream("testTemplateVariableConfigFile.json"));
    }


    @Test
    void getTemplateVariable() {
        final Map<String, Object> templateVariable = configFileTemplateVariableResource.getTemplateVariable();
        assertEquals(4, templateVariable.size());
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
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.putAll(configFileTemplateVariableResource.getTemplateVariable());
        Assertions.assertThrows(TemplateResolveException.class, ()-> dao.process(objectObjectHashMap));
    }

    @Test
    void testSuccessTemplate(){
        StandardEnvironment standardEnvironment=new StandardEnvironment();
        GenericTemplateContext genericTemplateContext=new GenericTemplateContext(standardEnvironment);
        final String testConfigTemplateResource = "testConfigTemplateResource";
        genericTemplateContext.registerTemplateDefinition(testConfigTemplateResource, TemplateDefinitionBuilder.build(
                DefaultNoHandleFunctionTemplate.class, "template/testTemplateVariable.template"));
        final Template dao = genericTemplateContext.getTemplate("testConfigTemplateResource");
        final Queue<Map<String, Object>> noShareVar = configFileTemplateVariableResource.getNoShareVar();
        Map<String,Object> dataModel=new HashMap<>();
        while(!noShareVar.isEmpty()){
            dataModel.putAll(configFileTemplateVariableResource.getTemplateVariable());
            Optional.ofNullable(noShareVar.poll()).ifPresent(dataModel::putAll);
            final String templateResult = dao.process(dataModel);
            Assertions.assertNotNull(templateResult);
        }
    }
}