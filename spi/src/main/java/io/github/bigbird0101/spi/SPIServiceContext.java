package io.github.bigbird0101.spi;

import io.github.bigbird0101.spi.annotation.Inject;
import io.github.bigbird0101.spi.inject.SPIServiceInjector;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI service loader for new instance for every call.
 *
 * @author Administrator
 */
class SPIServiceContext {
    private static volatile SPIServiceContext serviceContext;
    private SPIServiceContext() {
    }

    static SPIServiceContext getInstance(){
        if(null==serviceContext){
            synchronized (SPIServiceContext.class){
                if(null==serviceContext){
                    serviceContext=new SPIServiceContext();
                }
            }
        }
        return serviceContext;
    }

    private static final Map<Class<?>, Collection<Class<?>>> SERVICE_MAP = new ConcurrentHashMap<>();
    private static final Object CLAZZ_LOCK=new Object();
    private static final Object INSTANCE_LOCK =new Object();
    private static final Map<Class<?>, Collection<?>> SERVICE_INSTANCE_MAP = new ConcurrentHashMap<>();

    /**
     * Register SPI service into newServiceMap for new instance.
     *
     * @param service service type
     * @param <T>     type of service
     */
    private <T> void register(final Class<T> service) {
        for (T each : ServiceLoader.load(service)) {
            registerServiceClass(service, each);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void registerServiceClass(final Class<T> service, final T instance) {
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
        synchronized (INSTANCE_LOCK) {
            injectInstance(instance);
            instanceCollection.add(instance);
            SERVICE_INSTANCE_MAP.put(service, instanceCollection);
        }
    }

    private <T> void injectInstance(T instance) {
        for(Method method:instance.getClass().getMethods()){
            try {
                if (!isSetter(method) || null == method.getAnnotation(Inject.class)) {
                    continue;
                }
                try {
                    Class<?> pt = method.getParameterTypes()[0];
                    if (isPrimitives(pt)) {
                        continue;
                    }
                    String property = getSetterProperty(method);
                    SPIServiceInjector aggregate = SPIServiceLoader.loadService(SPIServiceInjector.class, "Aggregate");
                    Object object = aggregate.getInstance(pt, property);
                    if (object != null) {
                        method.invoke(instance, object);
                    }
                } catch (Exception ignored) {
                }
            }catch (Exception ignored){
            }
        }
    }

    public static boolean isPrimitives(Class<?> cls) {
        while (cls.isArray()) {
            cls = cls.getComponentType();
        }
        return isPrimitive(cls);
    }

    public static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == String.class || cls == Boolean.class || cls == Character.class
                || Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls);
    }

    private boolean isSetter(Method method) {
        return method.getName().startsWith("set")
                && method.getParameterTypes().length == 1
                && Modifier.isPublic(method.getModifiers());
    }

    /**
     * get properties name for setter, for instance: setVersion, return "version"
     * <p>
     * return "", if setter name with length less than 3
     */
    private String getSetterProperty(Method method) {
        return method.getName().length() > 3 ? method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4) : "";
    }

    /**
     * New service instances.
     *
     * @param service service class
     * @param <T>     type of service
     * @return service instances
     */
    @SuppressWarnings("unchecked")
    <T> Collection<T> loadServiceInstances(final Class<T> service) {
        if (null == SERVICE_INSTANCE_MAP.get(service)) {
            synchronized (INSTANCE_LOCK) {
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
    private <T> Collection<T> doLoadServiceInstances(Class<T> service) {
        return Collections.unmodifiableCollection(Optional.ofNullable((Collection<T>)
                SERVICE_INSTANCE_MAP.get(service)).orElse(Collections.emptyList()));
    }
}