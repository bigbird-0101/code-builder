package com.fpp.code.core.common;

import com.fpp.code.spi.NewInstanceServiceLoader;
import com.fpp.code.spi.TypeBasedSPIServiceLoader;

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
