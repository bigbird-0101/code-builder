package com.fpp.code.spi.order;

/**
 * Order aware.
 * 
 * @author Administrator
 * @param <T> type
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
