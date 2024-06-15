package io.github.bigbird0101.code.core.spi;

import io.github.bigbird0101.code.core.spi.order.OrderAware;

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
    private SPIServiceLoader(){

    }
    /**
     * New service instances.
     *
     * @param service service class
     * @param <T>     type of service
     * @return service instances
     */
    public static <T> Collection<T> loadServiceInstances(final Class<T> service) {
        return SPIServiceContext.getInstance().loadServiceInstances(service);
    }

    /**
     * Create new instance for type based SPI.
     *
     * @param type  SPI type
     * @param props SPI properties
     * @return SPI instance
     */
    public static <T extends TypeBasedSPI> T loadService(final Class<T> classType, final String type, final Properties props) {
        Collection<T> typeBasedServices = loadServices(classType,type);
        if (typeBasedServices.isEmpty()) {
            throw new RuntimeException(String.format("Invalid `%s` SPI type `%s`.", classType.getName(), type));
        }
        T result = typeBasedServices.iterator().next();
        result.init(props);
        return result;
    }

    /**
     * Create new instance for type based SPI.
     *
     * @param type  SPI type
     * @return SPI instance
     */
    public static <T extends TypeBasedSPI> T loadService(final Class<T> classType, final String type) {
        return loadService(classType,type,null);
    }

    private static <T extends TypeBasedSPI> Collection<T> loadServices(final Class<T> classType, final String type) {
        return SPIServiceContext.getInstance().loadServiceInstances(classType).stream()
                .filter(input -> type.equalsIgnoreCase(input.getType()))
                .collect(Collectors.toList());
    }

    public static <T extends OrderAware> LinkedList<T> loadServicesOrdered(final Class<T> classType){
        return loadServicesOrdered(classType,Comparator.comparingInt(OrderAware::getOrder));
    }

    public static <T extends OrderAware> LinkedList<T> loadServicesOrdered(final Class<T> classType,final Comparator<T> comparator){
        return SPIServiceContext.getInstance().loadServiceInstances(classType).stream()
                .sorted(comparator)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
