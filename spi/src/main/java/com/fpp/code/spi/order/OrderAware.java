package com.fpp.code.spi.order;

/**
 * Order aware.
 *
 * @param <T> type
 * @author Administrator
 */
public interface OrderAware<T> {

    /**
     * Get order of load.
     *
     * @return load order
     */
    int getOrder();

    /**
     * Get type.
     *
     * @return type
     */
    T getType();
}
