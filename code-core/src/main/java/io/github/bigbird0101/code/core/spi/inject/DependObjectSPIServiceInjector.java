package io.github.bigbird0101.code.core.spi.inject;

import io.github.bigbird0101.code.core.spi.inject.instance.InstanceContext;

/**
 * @author bigbird-0101
 * @date 2024-01-18 17:11
 */
public class DependObjectSPIServiceInjector implements SPIServiceInjector{
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> type, String name) {
        return (T) InstanceContext.getInstance().get(name,type).orElse(null);
    }

    @Override
    public String getType() {
        return "Depend";
    }
}
