package io.github.bigbird0101.code.core.template.domnode;

import io.github.bigbird0101.code.core.template.ConditionJudgeSupport;

import java.util.List;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 22:44:29
 */
public class IfCodeNode implements CodeNode{
    private final String test;

    private final CodeNode contents;

    private final ConditionJudgeSupport conditionJudgeSupport=new ConditionJudgeSupport();

    public IfCodeNode(String test, CodeNode contents) {
        this.test = test;
        this.contents = contents;
    }

    @Override
    public boolean apply(CodeNodeContext context) {
        List<String> targetKeyList=conditionJudgeSupport.checkFiled(context.getTemplateVariable(),test);
        if(conditionJudgeSupport.meetConditions(test,targetKeyList,context.getTemplateVariable())) {
            contents.apply(context);
            return true;
        }
        return false;
    }
}
