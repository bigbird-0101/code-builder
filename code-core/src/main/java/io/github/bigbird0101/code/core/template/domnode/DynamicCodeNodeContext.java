package io.github.bigbird0101.code.core.template.domnode;

import io.github.bigbird0101.code.core.config.Environment;

import java.util.Map;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 23:06:36
 */
public class DynamicCodeNodeContext implements CodeNodeContext{
    private StringBuilder stringBuilder=new StringBuilder();
    private final Map<String, Object> templateVariable;

    private final Environment environment;
    public DynamicCodeNodeContext(Map<String, Object> templateVariable, Environment environment) {
        this.templateVariable = templateVariable;
        this.environment = environment;
    }

    @Override
    public String getCode() {
        return stringBuilder.toString();
    }
    @Override
    public void appendCode(String code) {
         stringBuilder.append(code);
    }

    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public Map<String, Object> getTemplateVariable() {
        return templateVariable;
    }
}
