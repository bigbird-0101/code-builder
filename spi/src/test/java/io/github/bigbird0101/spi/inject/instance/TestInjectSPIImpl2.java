package io.github.bigbird0101.spi.inject.instance;

import io.github.bigbird0101.spi.annotation.Inject;

public class TestInjectSPIImpl2 implements TestInjectSPI {
    private Object myInsertObject;
    private TestInjectSPI testInjectSPIImpl1;
    @Inject
    public void setTestSPIImpl1(TestInjectSPI testInjectSPIImpl1) {
        this.testInjectSPIImpl1 = testInjectSPIImpl1;
    }

    @Inject
    public void setMyInsertObject(Object myInsertObject) {
        this.myInsertObject = myInsertObject;
    }

    public Object getMyInsertObject() {
        return myInsertObject;
    }

    public TestInjectSPI getTestSPIImpl1() {
        return testInjectSPIImpl1;
    }

    @Override
    public String getType() {
        return "TestSPIImpl2";
    }
}
