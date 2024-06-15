package io.github.bigbird0101.code.core.spi;

public interface TestInjectSPI extends TypeBasedSPI {
    default void test(){
        System.out.println(this.getClass().getName());
    }
}
