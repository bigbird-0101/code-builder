package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.config.aware.EnvironmentAware;
import io.github.bigbird0101.code.core.factory.config.TemplatePostProcessor;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 18:05:38
 */
public class TemplateResolverEnvironmentTemplatePostProcessor implements TemplatePostProcessor, EnvironmentAware {
    private Environment environment;
    @Override
    public Template postProcessBeforeInstantiation(Template template, String templateName) {
        if(template instanceof AbstractTemplate){
            AbstractTemplate abstractTemplate= (AbstractTemplate) template;
            final TemplateResolver templateResolver = abstractTemplate.getTemplateResolver();
            if (templateResolver instanceof AbstractAbstractTemplateResolver) {
                AbstractAbstractTemplateResolver abstractTemplateResolver = (AbstractAbstractTemplateResolver) templateResolver;
                abstractTemplateResolver.setEnvironment(environment);
            }
        }
        return template;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }
}
