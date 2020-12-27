package com.fpp.code.config;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/5/7 15:53
 */
public interface Config {
    /**
     * 获取属性值
     * @param propertyKey
     * @return
     */
    String getProperty(String propertyKey);
}
