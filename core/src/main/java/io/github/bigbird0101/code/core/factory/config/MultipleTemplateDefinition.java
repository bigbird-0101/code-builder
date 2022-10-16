package io.github.bigbird0101.code.core.factory.config;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Administrator
 */
public interface MultipleTemplateDefinition extends Cloneable, Serializable {
    Set<String> getTemplateNames();
}
