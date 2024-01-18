package io.github.bigbird0101.spi.inject.instance;

import io.github.bigbird0101.spi.SPIServiceLoader;
import io.github.bigbird0101.spi.inject.SPIServiceInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author bigbird-0101
 * @date 2024-01-18 21:08
 */
public class InjectTest {
    private InstanceContext instanceContextUnderTest;

    @BeforeEach
    void setUp() {
        instanceContextUnderTest = InstanceContext.getInstance();
    }

    @Test
    public void test(){
        Object object = new Object();
        instanceContextUnderTest.register("myInsertObject", object);
        SPIServiceInjector aggregate = SPIServiceLoader.loadService(SPIServiceInjector.class, "Aggregate");
        Object myInsertObject = aggregate.getInstance(Object.class, "myInsertObject");
        assertEquals(object,myInsertObject);
        TestInjectSPIImpl2 testSPIImpl2 = (TestInjectSPIImpl2) SPIServiceLoader.loadService(TestInjectSPI.class, "TestSPIImpl2");
        Object myInsertObject1 = testSPIImpl2.getMyInsertObject();
        assertEquals(object,myInsertObject1);
        TestInjectSPI testSPIImpl1 = testSPIImpl2.getTestSPIImpl1();
        assertNotNull(testSPIImpl1);
    }
}
