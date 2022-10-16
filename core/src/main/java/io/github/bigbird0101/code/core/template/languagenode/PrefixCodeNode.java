package io.github.bigbird0101.code.core.template.languagenode;

/**
 * 表示整个模板的前缀节点 用于可控制方法的模板 作为前缀
 * 类似
 * <prefix>
 *    factory
 * </prefix>
 *   .
 *   .
 *   .
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 11:03:49
 */
public class PrefixCodeNode extends WrapperCodeNode{
    public PrefixCodeNode(CodeNode codeNode) {
        super(codeNode);
    }
}
