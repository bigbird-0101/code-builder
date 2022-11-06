package io.github.bigbird0101.code.core.config;

import io.github.bigbird0101.code.core.exception.CodeConfigException;

/**
 * @author fpp
 * @version 1.0
 * @since 2020/12/22 11:14
 */
public interface Environment extends PropertyResolver,RefreshPropertySourceSerialize,RemovePropertySourceSerialize{
    /**
     * 获取属性资源
     * @return 属性资源
     */
    PropertySources getPropertySources();

    /**
     * 解析环境
     * @throws CodeConfigException CodeConfigException
     */
    void parse() throws CodeConfigException;

    /**
     * 刷新环境
     * @throws CodeConfigException CodeConfigException
     */
    void refresh() throws CodeConfigException;
}
