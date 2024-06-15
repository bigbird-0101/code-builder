package io.github.bigbird0101.code.core.spi;

import io.github.bigbird0101.code.core.spi.inject.instance.InstanceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        Optional<Object> abcd = instanceContextUnderTest.get("abcd");
        assertEquals(abcd.get(),object);
        Optional<Object> abcd1 = instanceContextUnderTest.get("abcd", String.class);
        assertNull(abcd1.orElse(null));
        instanceContextUnderTest.destroy();
    }
}
