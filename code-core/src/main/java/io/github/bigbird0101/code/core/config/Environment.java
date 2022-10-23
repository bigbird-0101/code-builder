package io.github.bigbird0101.code.core.config;

import io.github.bigbird0101.code.core.exception.CodeConfigException;

/**
 * @author fpp
 * @version 1.0
 * @since 2020/12/22 11:14
 */
public interface Environment extends PropertyResolver,RefreshPropertySourceSerialize,RemovePropertySourceSerialize{
    PropertySources getPropertySources();
    void parse() throws CodeConfigException;
    void refresh() throws CodeConfigException;
}
