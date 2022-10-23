package io.github.bigbird0101.code.core.template.languagenode;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 22:09:20
 */
public interface CodeNode {
    /**
     * code节点解析
     * @param context CodeNode上下文
     * @return 是否解析成功
     */
    boolean apply(CodeNodeContext context);
}
