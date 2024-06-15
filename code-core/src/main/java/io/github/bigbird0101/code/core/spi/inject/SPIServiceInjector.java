package io.github.bigbird0101.code.core.spi.inject;

import io.github.bigbird0101.code.core.spi.TypeBasedSPI;

/**
 * SPI Service Injector
 * @author Lily
 */
public interface SPIServiceInjector extends TypeBasedSPI {
    /**
     * get instance
     * @param type instance type
     * @param name instance name
     * @return instance
     * @param <T>
     */
    <T> T getInstance(final Class<T> type, final String name);
}