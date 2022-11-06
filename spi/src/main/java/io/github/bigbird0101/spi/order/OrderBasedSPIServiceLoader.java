package io.github.bigbird0101.spi.order;

import io.github.bigbird0101.spi.NewInstanceServiceLoader;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-05 22:56:13
 */
public abstract class OrderBasedSPIServiceLoader <T extends OrderAware>{
    private final Class<T> classType;

    protected OrderBasedSPIServiceLoader(Class<T> classType) {
        this.classType = classType;
    }

    public final LinkedList<T> newServices(){
        return NewInstanceServiceLoader.newServiceInstances(classType).stream()
                .sorted(Comparator.comparingInt(OrderAware::getOrder))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
