package io.github.bigbird0101.spi.inject.instance;

import io.github.bigbird0101.spi.TypeBasedSPI;

public interface TestInjectSPI extends TypeBasedSPI {
    default void test(){
        System.out.println(this.getClass().getName());
    }
}
