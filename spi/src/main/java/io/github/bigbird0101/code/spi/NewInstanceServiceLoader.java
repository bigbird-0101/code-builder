package io.github.bigbird0101.code.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * SPI service loader for new instance for every call.
 *
 * @author Administrator
 */
public final class NewInstanceServiceLoader {
    private NewInstanceServiceLoader() {
    }
    private static final Map<Class<?>, Collection<Class<?>>> SERVICE_MAP = new HashMap<>();

    /**
     * Register SPI service into map for new instance.
     *
     * @param service service type
     * @param <T>     type of service
     */
    public static <T> void register(final Class<T> service) {
        for (T each : ServiceLoader.load(service)) {
            registerServiceClass(service, each);
        }
    }

    private static <T> void registerServiceClass(final Class<T> service, final T instance) {
        Collection<Class<?>> serviceClasses = SERVICE_MAP.get(service);
        if (null == serviceClasses) {
            serviceClasses = new LinkedHashSet<>();
        }
        serviceClasses.add(instance.getClass());
        SERVICE_MAP.put(service, serviceClasses);
    }

    /**
     * New service instances.
     *
     * @param service service class
     * @param <T>     type of service
     * @return service instances
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> newServiceInstances(final Class<T> service) {
        Collection<T> result = new LinkedList<>();
        if (null == SERVICE_MAP.get(service)) {
            return result;
        }
        for (Class<?> each : SERVICE_MAP.get(service)) {
            try {
                result.add((T) each.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}