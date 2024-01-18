package io.github.bigbird0101.spi.inject.instance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstanceContextTest {

    private InstanceContext instanceContextUnderTest;

    @BeforeEach
    void setUp() {
        instanceContextUnderTest = InstanceContext.getInstance();
    }

    @Test
    void test(){
        Object object = new Object();
        instanceContextUnderTest.register("abcd", object);
    }
}
