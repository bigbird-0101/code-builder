package io.github.bigbird0101.code.core.template.languagenode;

import java.util.List;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 22:41:56
 */
public class MixCodeNode implements CodeNode{
    private final List<CodeNode> contents;

    public MixCodeNode(List<CodeNode> contents) {
        this.contents = contents;
    }

    @Override
    public boolean apply(CodeNodeContext context) {
        contents.forEach(s->s.apply(context));
        return true;
    }

    public List<CodeNode> getContents() {
        return contents;
    }
}
