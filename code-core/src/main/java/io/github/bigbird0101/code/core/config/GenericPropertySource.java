package io.github.bigbird0101.code.core.config;

/**
 * 普通的属性资源
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-06 17:47:28
 */
public class GenericPropertySource<T> extends PropertySource<T>{
    public GenericPropertySource(String name, T source) {
        super(name, source);
    }
}
