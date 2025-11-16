package io.github.bigbird0101.code.core.template.domnode;

import java.util.List;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 22:41:56
 */
public record MixCodeNode(List<CodeNode> contents) implements CodeNode {

    @Override
    public boolean apply(CodeNodeContext context) {
        contents.forEach(s -> s.apply(context));
        return true;
    }
}
