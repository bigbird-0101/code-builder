package io.github.bigbird0101.spi;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-05 23:25:00
 */
public class GenericTypeBasedSPIServiceLoader extends TypeBasedSPIServiceLoader{
    protected GenericTypeBasedSPIServiceLoader(Class classType) {
        super(classType);
    }
}
