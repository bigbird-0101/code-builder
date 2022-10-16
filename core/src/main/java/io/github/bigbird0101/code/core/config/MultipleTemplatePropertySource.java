package io.github.bigbird0101.code.core.config;

import io.github.bigbird0101.code.core.template.MultipleTemplate;

/**
 * @author fpp
 * @version 1.0
 */
public class MultipleTemplatePropertySource extends PropertySource<MultipleTemplate>{
    public MultipleTemplatePropertySource(String name, MultipleTemplate source) {
        super(name, source);
    }
}
