package io.github.bigbird0101.spi.inject;

import io.github.bigbird0101.spi.inject.instance.InstanceContext;

/**
 * @author pengpeng_fu@infinova.com.cn
 * @date 2024-01-18 17:11
 */
public class DependObjectSPIServiceInjector implements SPIServiceInjector{
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> type, String name) {
        return (T) InstanceContext.getInstance().get(name,type).get();
    }

    @Override
    public String getType() {
        return "Depend";
    }
}
