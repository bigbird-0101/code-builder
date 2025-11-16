package io.github.bigbird0101.code.core.config;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Administrator
 * @param <T>
 */
public abstract class PropertySource<T> implements Serializable {

    private final String name;
    private final T source;

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
        return new ObjectPropertySources(name,new Object());
    }

    public static class StubPropertySource extends PropertySource<Object> {

        public StubPropertySource(String name) {
            super(name, new Object());
        }
    }


    static class ComparisonPropertySource extends StubPropertySource {

        private static final String USAGE_ERROR =
                "ComparisonPropertySource instances are for use with collection comparison only";

        ComparisonPropertySource(String name) {
            super(name);
        }

        @Override
        public Object getSource() {
            throw new UnsupportedOperationException(USAGE_ERROR);
        }

    }

    @Override
    public String toString() {
        return "PropertySource{" +
                "name='" + name + '\'' +
                ", source=" + source +
                '}';
    }
}
