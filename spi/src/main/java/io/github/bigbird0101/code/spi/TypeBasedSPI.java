package io.github.bigbird0101.code.spi;

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
     * Get properties.
     *
     * @return properties of algorithm
     */
    Properties getProperties();

    /**
     * Set properties.
     *
     * @param properties properties of algorithm
     */
    void setProperties(Properties properties);
}