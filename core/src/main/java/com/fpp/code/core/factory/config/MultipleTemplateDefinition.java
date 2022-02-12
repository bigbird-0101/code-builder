package com.fpp.code.core.factory.config;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Administrator
 */
public interface MultipleTemplateDefinition extends Cloneable, Serializable {
    Set<String> getTemplateNames();
}
