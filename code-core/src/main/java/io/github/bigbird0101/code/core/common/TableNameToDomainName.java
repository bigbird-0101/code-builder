package io.github.bigbird0101.code.core.common;

import io.github.bigbird0101.spi.TypeBasedSPI;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-12 14:55:13
 */
public interface TableNameToDomainName extends TypeBasedSPI{

    String DEFAULT="CAMEL_CASE";

    /**
     * 表名构建domainName名
     * @param tableName 表名
     * @return domainName名
     */
    String buildDomainName(String tableName);
}
