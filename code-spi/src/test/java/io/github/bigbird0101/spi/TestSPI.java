package io.github.bigbird0101.spi;

public interface TestSPI extends TypeBasedSPI{
    default void test(){
        System.out.println(this.getClass().getName());
    }
}
