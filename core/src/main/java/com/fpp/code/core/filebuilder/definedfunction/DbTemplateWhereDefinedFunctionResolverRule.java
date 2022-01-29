package com.fpp.code.core.filebuilder.definedfunction;

import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class DbTemplateWhereDefinedFunctionResolverRule implements DefinedFunctionResolverRule {
    private static org.apache.logging.log4j.Logger logger= LogManager.getLogger(DbTemplateWhereDefinedFunctionResolverRule.class);

    public static List<String> types;
    static {
        types= Stream.of(Types.class.getFields()).map(Field::getName).collect(Collectors.toList());
    }
}
