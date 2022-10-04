package com.fpp.code.core.template.languagenode;

import java.util.Map;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 22:31:56
 */
public interface CodeNodeContext {
    /**
     * 获取最终的代码
     * @return
     */
    String getCode();

    /**
     * 拼接code
     * @param code
     */
    void appendCode(String code);
    /**
     * 获取模板变量 k-v map
     * @return 模板变量 k-v map
     */
    Map<String, Object> getTemplateVariable();
}
