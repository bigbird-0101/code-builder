package io.github.bigbird0101.code.core.spi.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author bigbird-0101
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
public @interface Inject {

}