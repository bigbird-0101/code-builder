package io.github.bigbird0101.code.core.template.domnode;

import io.github.bigbird0101.code.util.Utils;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.text.CharSequenceUtil.removePrefix;
import static cn.hutool.core.text.StrPool.LF;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 22:38:37
 */
public class StaticTextCodeNode implements CodeNode{
    private final String text;
    public StaticTextCodeNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(CodeNodeContext context) {
        if(isBlank(text)){
            return true;
        }
        if(!LF.equals(text)){
            if(text.startsWith(LF)){
                String temp= removePrefix(text, LF);
                final String removeSuffixEmpty = Utils.removeSuffixEmpty(temp);
                if(removeSuffixEmpty.endsWith(LF)) {
                    context.appendCode(removeSuffixEmpty);
                }else{
                    context.appendCode(temp);
                }
            }else {
                context.appendCode(text);
            }
        }
        return true;
    }
}
