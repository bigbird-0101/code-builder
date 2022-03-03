package com.fpp.code.core.template;

import java.util.Set;

/**
 * 有依赖的模板
 * @author Administrator
 */
public interface HaveDependTemplate extends Template {
    /**
     * 获取依赖的模板名
     * @return
     */
    Set<String> getDependTemplates();
}
