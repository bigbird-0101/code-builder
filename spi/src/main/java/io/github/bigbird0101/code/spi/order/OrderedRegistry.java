package io.github.bigbird0101.code.spi.order;

import io.github.bigbird0101.code.spi.NewInstanceServiceLoader;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Ordered registry.
 * @author Administrator
 */
public final class OrderedRegistry {
    private OrderedRegistry() {
    }

    /**
     * Get registered classes.
     * 
     * @param orderAwareClass class of order aware
     * @param <T> type of order aware class
     * @return registered classes
     */
    @SuppressWarnings("unchecked")
    public static <T extends OrderAware<?>> Collection<Class<T>> getRegisteredClasses(final Class<T> orderAwareClass) {
        Map<Integer, Class<T>> result = new TreeMap<>();
        for (T each : NewInstanceServiceLoader.newServiceInstances(orderAwareClass)) {
            result.put(each.getOrder(), (Class<T>) each.getClass());
        }
        return result.values();
    }
}