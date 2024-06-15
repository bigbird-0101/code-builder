package io.github.bigbird0101.code.core.spi.order;

/**
 * Order aware.
 *
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