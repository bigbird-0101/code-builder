package io.github.bigbird0101.spi;

import java.util.*;

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
     * Register SPI service into newServiceMap for new instance.
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
            synchronized (SERVICE_MAP) {
                serviceClasses = SERVICE_MAP.get(service);
                if(null==serviceClasses) {
                    serviceClasses = new LinkedHashSet<>();
                }
            }
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
    public static <T> Collection<T> newServiceInstances(final Class<T> service) {
        if (null == SERVICE_MAP.get(service)) {
            register(service);
            return doNewServiceInstances(service);
        }
        return doNewServiceInstances(service);
    }

    @SuppressWarnings("unchecked")
    private static <T> Collection<T> doNewServiceInstances(Class<T> service) {
        Collection<T> result = new LinkedList<>();
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