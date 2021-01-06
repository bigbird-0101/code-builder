package com.fpp.code.config;

import java.util.Objects;

public abstract class PropertySource<T> {


    private String name;
    private T source;

    public PropertySource(String name, T source) {
        Objects.requireNonNull(name,"name must be not null");
        Objects.requireNonNull(name,"source must be not null");
        this.name = name;
        this.source = source;
    }

    public T getSource() {
        return this.source;
    }

    public String getName(){
        return this.name;
    }


    public static PropertySource<?> named(String name) {
        return new ComparisonPropertySource(name);
    }

    public static class StubPropertySource extends PropertySource<Object> {

        public StubPropertySource(String name) {
            super(name, new Object());
        }
    }


    static class ComparisonPropertySource extends StubPropertySource {

        private static final String USAGE_ERROR =
                "ComparisonPropertySource instances are for use with collection comparison only";

        public ComparisonPropertySource(String name) {
            super(name);
        }

        @Override
        public Object getSource() {
            throw new UnsupportedOperationException(USAGE_ERROR);
        }

    }
}
