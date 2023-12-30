package io.github.bigbird0101.code.core.template.domnode;

/**
 * 作为可控制方法模板当中的 方法节点语法
 * 例如
 * &lt;function name='factory'&gt;
 *
 * &lt;/function&gt;
 *  .
 *  .
 *  .
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 11:07:13
 */
public class FunctionCodeNode implements CodeNode{
    /**
     * 方法的别名
     */
    private final String name;
    private final CodeNode contents;

    public FunctionCodeNode(String name, CodeNode contents) {
        this.name = name;
        this.contents = contents;
    }


    @Override
    public boolean apply(CodeNodeContext context) {
        return contents.apply(context);
    }

    public String getName() {
        return name;
    }
}
