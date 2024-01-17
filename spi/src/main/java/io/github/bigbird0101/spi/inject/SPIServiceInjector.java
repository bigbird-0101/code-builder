package io.github.bigbird0101.spi.inject;

public interface SPIServiceInjector {
    <T> T getInstance(final Class<T> type, final String name);
}