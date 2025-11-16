package io.github.bigbird0101.code.core.template.domnode;

import java.util.Map;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 22:31:56
 */
public interface CodeNodeContext {
    /**
     * 获取最终的代码
     * @return 最终的代码
     */
    String getCode();

    /**
     * 拼接code
     * @param code 待拼接的代码
     */
    void appendCode(String code);
    /**
     * 获取模板变量 k-v newServiceMap
     * @return 模板变量 k-v newServiceMap
     */
    Map<String, Object> getTemplateVariable();
}
