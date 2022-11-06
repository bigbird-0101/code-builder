package io.github.bigbird0101.spi;

import io.github.bigbird0101.spi.order.OrderAware;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-05 23:14:33
 */
public final class SPIServiceLoader {
    /**
     * New service instances.
     *
     * @param service service class
     * @param <T>     type of service
     * @return service instances
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> newServiceInstances(final Class<T> service) {
        return NewInstanceServiceLoader.newServiceInstances(service);
    }

    /**
     * Create new instance for type based SPI.
     *
     * @param type  SPI type
     * @param props SPI properties
     * @return SPI instance
     */
    public static <T extends TypeBasedSPI> T newService(final Class<T> classType,final String type, final Properties props) {
        Collection<T> typeBasedServices = loadTypeBasedServices(classType,type);
        if (typeBasedServices.isEmpty()) {
            throw new RuntimeException(String.format("Invalid `%s` SPI type `%s`.", classType.getName(), type));
        }
        T result = typeBasedServices.iterator().next();
        result.setProperties(props);
        return result;
    }

    private static <T extends TypeBasedSPI> Collection<T> loadTypeBasedServices(final Class<T> classType, final String type) {
        return NewInstanceServiceLoader.newServiceInstances(classType).stream()
                .filter(input -> type.equalsIgnoreCase(input.getType()))
                .collect(Collectors.toList());
    }

    public static <T extends OrderAware> LinkedList<T> newServicesOrdered(final Class<T> classType){
        return NewInstanceServiceLoader.newServiceInstances(classType).stream()
                .sorted(Comparator.comparingInt(OrderAware::getOrder))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
