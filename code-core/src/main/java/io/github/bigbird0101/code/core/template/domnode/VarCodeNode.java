package io.github.bigbird0101.code.core.template.domnode;

import io.github.bigbird0101.code.core.template.SimpleAbstractTemplateResolver;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 22:44:29
 */
public class VarCodeNode implements CodeNode {
    private static final SimpleAbstractTemplateResolver SIMPLE_TEMPLATE_RESOLVER = SimpleAbstractTemplateResolver.getInstance();
    private final String name;
    private final String value;

    public VarCodeNode(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean apply(CodeNodeContext context) {
        String resolverValue = SIMPLE_TEMPLATE_RESOLVER.resolver(value, context.getTemplateVariable());
        context.getTemplateVariable().put(name, resolverValue);
        return true;
    }
}
