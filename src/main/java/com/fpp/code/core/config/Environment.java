package com.fpp.code.core.config;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/12/22 11:14
 */
public interface Environment extends PropertyResolver,RefreshPropertySourceSerialize{
    PropertySources getPropertySources();
    void parse() throws CodeConfigException;
    void refresh() throws CodeConfigException;
}
