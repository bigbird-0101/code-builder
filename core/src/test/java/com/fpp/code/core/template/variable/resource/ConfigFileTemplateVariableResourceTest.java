package com.fpp.code.core.template.variable.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
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
                        .getResourceAsStream("testConfigFile.properties"));
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
    }
}