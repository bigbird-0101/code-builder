package io.github.bigbird0101.code.core.context;

import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.TemplateScanner;
import io.github.bigbird0101.code.core.template.Template;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-28 21:06:03
 */
public abstract class AbstractRefreshTemplateContext extends AbstractTemplateContext{

    private Boolean allowTemplateDefinitionOverriding=true;

    private volatile DefaultListableTemplateFactory templateFactory;

    public AbstractRefreshTemplateContext(Environment environment) {
        super(environment);
    }

    public AbstractRefreshTemplateContext(Environment environment, TemplateScanner allTypeTemplateScanner) {
        super(environment, allTypeTemplateScanner);
    }

    public Boolean getAllowTemplateDefinitionOverriding() {
        return allowTemplateDefinitionOverriding;
    }

    public void setAllowTemplateDefinitionOverriding(Boolean allowTemplateDefinitionOverriding) {
        this.allowTemplateDefinitionOverriding = allowTemplateDefinitionOverriding;
    }

    @Override
    protected void refreshTemplateFactory() {
        if(hasTemplateFactory()){
            //destroy templates
            destroyTemplates();
            closeTemplateFactory();
        }
        this.templateFactory= createTemplateFactory();
        this.templateFactory.setAllowTemplateDefinitionOverriding(allowTemplateDefinitionOverriding);
        this.templateFactory.setEnvironment(getEnvironment());
    }

    private DefaultListableTemplateFactory createTemplateFactory() {
        return new DefaultListableTemplateFactory();
    }

    @Override
    public void destroyTemplates() {
        getTemplateFactory().destroyTemplates();
    }

    protected final void closeTemplateFactory() {
        DefaultListableTemplateFactory templateFactory = this.templateFactory;
        if (templateFactory != null) {
            this.templateFactory = null;
        }
    }

    protected boolean hasTemplateFactory(){
        return this.templateFactory!=null;
    }

    @Override
    public DefaultListableTemplateFactory getTemplateFactory() {
        DefaultListableTemplateFactory templateFactory = this.templateFactory;
        if (templateFactory == null) {
            throw new IllegalStateException("TemplateFactory not initialized or already closed - " +
                    "call 'refresh' before accessing beans via the TemplateContext");
        }
        return templateFactory;
    }

    /**
     * 刷新所有的模板
     */
    public void refreshAllTemplate(){
        getTemplateNames().forEach(s->{
            final Template template = getTemplate(s);
            template.refresh();
        });
    }
}
