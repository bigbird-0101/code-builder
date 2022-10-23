package io.github.bigbird0101.code.core.common;

import io.github.bigbird0101.spi.NewInstanceServiceLoader;
import io.github.bigbird0101.spi.TypeBasedSPIServiceLoader;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-12 15:16:29
 */
public class TableNameToDomainNameServiceLoader extends TypeBasedSPIServiceLoader<TableNameToDomainName> {
    static{
        NewInstanceServiceLoader.register(TableNameToDomainName.class);
    }
    protected TableNameToDomainNameServiceLoader(Class<TableNameToDomainName> classType) {
        super(classType);
    }
}
