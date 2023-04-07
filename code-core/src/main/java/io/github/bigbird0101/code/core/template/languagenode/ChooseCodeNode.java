package io.github.bigbird0101.code.core.template.languagenode;

import java.util.List;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 22:44:29
 */
public class ChooseCodeNode implements CodeNode{
    private final List<CodeNode> whenCodes;
    private final CodeNode contentsOtherWise;

    public ChooseCodeNode(List<CodeNode> whenCodes, CodeNode contentsOtherWise) {
        this.whenCodes = whenCodes;
        this.contentsOtherWise = contentsOtherWise;
    }

    @Override
    public boolean apply(CodeNodeContext context) {
        for(CodeNode codeNode:whenCodes){
            if(codeNode.apply(context)){
                return true;
            }
        }
        if(contentsOtherWise.apply(context)){
            return true;
        }
        return false;
    }
}
