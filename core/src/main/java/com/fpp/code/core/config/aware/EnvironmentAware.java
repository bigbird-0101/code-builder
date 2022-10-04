package com.fpp.code.core.config.aware;

import com.fpp.code.core.config.Environment;

/**
 * 框架环境感知
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 19:25:54
 */
public interface EnvironmentAware {
    /**
     * 框架环境感知
     * @param environment
     */
    void setEnvironment(Environment environment);
}
