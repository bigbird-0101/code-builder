package io.github.bigbird0101.spi;

import java.util.*;

/**
 * SPI service loader for new instance for every call.
 *
 * @author Administrator
 */
class SPIServiceContext {
    private SPIServiceContext() {
    }
    private static final Map<Class<?>, Collection<Class<?>>> SERVICE_MAP = new HashMap<>();

    private static final Object CLAZZ_LOCK=new Object();

    private static final Object INSTANCE_Lock=new Object();
    private static final Map<Class<?>, Collection<?>> SERVICE_INSTANCE_MAP = new HashMap<>();

    /**
     * Register SPI service into newServiceMap for new instance.
     *
     * @param service service type
     * @param <T>     type of service
     */
    private static <T> void register(final Class<T> service) {
        for (T each : ServiceLoader.load(service)) {
            registerServiceClass(service, each);
        }
    }

    @SuppressWarnings("unchecked")
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
        synchronized (CLAZZ_LOCK) {
            serviceClasses.add(instance.getClass());
            SERVICE_MAP.put(service, serviceClasses);
        }

        Collection<T> instanceCollection = (Collection<T>) SERVICE_INSTANCE_MAP.get(service);
        if(null==instanceCollection){
            synchronized (SERVICE_INSTANCE_MAP){
                instanceCollection = (Collection<T>) SERVICE_INSTANCE_MAP.get(service);
                if(null==instanceCollection) {
                    instanceCollection = new LinkedHashSet<>();
                }
            }
        }
        synchronized (INSTANCE_Lock) {
            instanceCollection.add(instance);
            SERVICE_INSTANCE_MAP.put(service, instanceCollection);
        }
    }

    /**
     * New service instances.
     *
     * @param service service class
     * @param <T>     type of service
     * @return service instances
     */
    @SuppressWarnings("unchecked")
    static <T> Collection<T> loadServiceInstances(final Class<T> service) {
        if (null == SERVICE_INSTANCE_MAP.get(service)) {
            synchronized (INSTANCE_Lock) {
                Collection<T> instanceCollection = (Collection<T>) SERVICE_INSTANCE_MAP.get(service);
                if(null==instanceCollection) {
                    register(service);
                }
            }
            return doLoadServiceInstances(service);
        }
        return doLoadServiceInstances(service);
    }

    @SuppressWarnings("unchecked")
    private static <T> Collection<T> doLoadServiceInstances(Class<T> service) {
        return Collections.unmodifiableCollection(Optional.ofNullable((Collection<T>)
                SERVICE_INSTANCE_MAP.get(service)).orElse(Collections.emptyList()));
    }
}