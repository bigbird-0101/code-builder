package com.fpp.code.core.filebuilder.definedfunction;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Administrator
 */
public abstract class DbTemplateWhereDefinedFunctionResolverRule extends AbstractDefinedFunctionResolverRule {

    public static List<String> MYSQL_TYPES;
    static {
        MYSQL_TYPES = Stream.of(Types.class.getFields()).map(Field::getName).collect(Collectors.toList());
    }
}
