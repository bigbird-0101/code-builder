package io.github.bigbird0101.spi.inject;

import io.github.bigbird0101.spi.SPIServiceLoader;
import io.github.bigbird0101.spi.TypeBasedSPI;

public class DefaultSPIServiceInjector implements SPIServiceInjector{
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> type, String name) {
        if(type.isAssignableFrom(TypeBasedSPI.class)){
            Class<TypeBasedSPI> typeBasedSPIClass= (Class<TypeBasedSPI>) type;
            return (T) SPIServiceLoader.loadService(typeBasedSPIClass,name);
        }
        return null;
    }
}