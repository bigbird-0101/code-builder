package com.fpp.code.core.config;

public interface RefreshPropertySourceSerialize {
   <T> void refreshPropertySourceSerialize(PropertySource<T>... propertySources);
}
