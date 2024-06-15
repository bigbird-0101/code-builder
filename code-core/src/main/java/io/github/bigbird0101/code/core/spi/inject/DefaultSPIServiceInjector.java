package io.github.bigbird0101.code.core.spi.inject;

import io.github.bigbird0101.code.core.spi.SPIServiceLoader;
import io.github.bigbird0101.code.core.spi.TypeBasedSPI;

/**
 * @author bigbird-0101
 */
public class DefaultSPIServiceInjector implements SPIServiceInjector{
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> type, String name) {
        if(TypeBasedSPI.class.isAssignableFrom(type)){
            Class<TypeBasedSPI> spiClass = (Class<TypeBasedSPI>) type;
            return (T) SPIServiceLoader.loadService(spiClass,name);
        }
        return null;
    }

    @Override
    public String getType() {
        return "Default";
    }
}