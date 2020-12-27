package com.fpp.code.template;

import java.util.List;

/**
 * 普通的模板工厂
 */
public class GenericTemplateFactory extends AbstractTemplateFactory{

    public GenericTemplateFactory() {
        super(null);
    }

    public GenericTemplateFactory(List<TemplateConfigDomain> templateConfig) {
        super(templateConfig);
    }
}
