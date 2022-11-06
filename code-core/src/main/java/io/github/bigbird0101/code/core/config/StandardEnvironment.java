package io.github.bigbird0101.code.core.config;

/**
 * @author fpp
 * @version 1.0
 */
public class StandardEnvironment extends AbstractEnvironment {
    public StandardEnvironment() {
        init();
    }

    private void init() {
        setCoreConfigPath("META-INF/code.properties");
        setTemplatesPath("META-INF/templates");
        setTemplateConfigPath("META-INF/templates.json");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
