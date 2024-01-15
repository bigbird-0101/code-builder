package io.github.bigbird0101.spi;

import java.util.Properties;

/**
 * Base algorithm SPI.
 *
 * @author Administrator
 */
public interface TypeBasedSPI {

    /**
     * Get algorithm type.
     *
     * @return type
     */
    String getType();
    /**
     * Judge whether default service provider.
     *
     * @return is default service provider or not
     */
    default boolean isDefault() {
        return false;
    }

    /**
     * Set properties.
     *
     * @param properties properties of algorithm
     */
    default void setProperties(Properties properties){

    }
}