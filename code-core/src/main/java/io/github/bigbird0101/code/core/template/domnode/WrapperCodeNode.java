package io.github.bigbird0101.code.core.template.domnode;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 11:02:02
 */
public class WrapperCodeNode implements CodeNode{
    private final CodeNode codeNode;

    public WrapperCodeNode(CodeNode codeNode) {
        this.codeNode = codeNode;
    }

    @Override
    public boolean apply(CodeNodeContext context) {
        return codeNode.apply(context);
    }

    public CodeNode getCodeNode() {
        return codeNode;
    }
}
