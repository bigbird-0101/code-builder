package io.github.bigbird0101.code.core.context;

import io.github.bigbird0101.code.core.event.EventMulticaster;
import io.github.bigbird0101.code.core.factory.ConfigurableListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.ConfigurableTemplateFactory;
import io.github.bigbird0101.code.core.factory.config.EnvironmentCapable;

/**
 * @author Administrator
 */
public interface TemplateContext extends ConfigurableTemplateFactory, EnvironmentCapable, EventMulticaster {
    /**
     * 获取模板工厂
     *
     * @return
     */
    ConfigurableListableTemplateFactory getTemplateFactory();
}
