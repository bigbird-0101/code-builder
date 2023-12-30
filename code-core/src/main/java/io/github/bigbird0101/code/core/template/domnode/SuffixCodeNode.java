package io.github.bigbird0101.code.core.template.domnode;

/**
 * 表示整个模板的后缀节点 用于可控制方法的模板 作为后缀
 * 类似
 * <p>
 * &lt;suffix&gt;
 *    factory
 * &lt;/suffix&gt;
 * </p>
 *   .
 *   .
 *   .
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 10:58:33
 */
public class SuffixCodeNode extends WrapperCodeNode {
    public SuffixCodeNode(CodeNode codeNode) {
        super(codeNode);
    }
}
