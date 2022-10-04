package com.fpp.code.core.template;

import com.fpp.code.core.config.Environment;
import com.fpp.code.core.config.aware.EnvironmentAware;
import com.fpp.code.core.factory.config.TemplatePostProcessor;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 18:05:38
 */
public class TemplateResolverEnvironmentTemplatePostProcessor implements TemplatePostProcessor, EnvironmentAware {
    private Environment environment;
    @Override
    public Template postProcessAfterInstantiation(Template template, String templateName) {
        if(template instanceof AbstractTemplate){
            AbstractTemplate abstractTemplate= (AbstractTemplate) template;
            final TemplateResolver templateResolver = abstractTemplate.getTemplateResolver();
            if(templateResolver instanceof AbstractTemplateResolver){
                AbstractTemplateResolver abstractTemplateResolver= (AbstractTemplateResolver) templateResolver;
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
