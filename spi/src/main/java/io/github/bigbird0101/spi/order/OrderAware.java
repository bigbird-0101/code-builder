package io.github.bigbird0101.spi.order;

/**
 * Order aware.
 *
 * @param <T> type
 * @author Administrator
 */
public interface OrderAware{

    /**
     * Get order of load.
     *
     * @return load order
     */
    int getOrder();
}