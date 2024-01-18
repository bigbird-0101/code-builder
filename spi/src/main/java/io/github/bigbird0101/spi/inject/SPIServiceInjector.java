package io.github.bigbird0101.spi.inject;

import io.github.bigbird0101.spi.TypeBasedSPI;

public interface SPIServiceInjector extends TypeBasedSPI {
    <T> T getInstance(final Class<T> type, final String name);
}