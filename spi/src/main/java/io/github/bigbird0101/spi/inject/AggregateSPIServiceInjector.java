package io.github.bigbird0101.spi.inject;

import io.github.bigbird0101.spi.SPIServiceLoader;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class AggregateSPIServiceInjector implements SPIServiceInjector{
    private final Collection<SPIServiceInjector> injectors;

    public AggregateSPIServiceInjector() {
        injectors = SPIServiceLoader.loadServiceInstances(SPIServiceInjector.class)
                .stream().collect(Collectors.collectingAndThen(Collectors.toList(),Collections::unmodifiableList));
    }

    @Override
    public <T> T getInstance(Class<T> type, String name) {
        return injectors.stream().map(s->s.getInstance(type,name)).findFirst().orElse(null);
    }
}
