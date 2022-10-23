package io.github.bigbird0101.code.core.template.targetfile;

public class TemplateFilePrefixNameStrategyFactory {

    public TargetFilePrefixNameStrategy getTemplateFilePrefixNameStrategy(int type){
        if (type == 1) {
            return new DefaultTargetFilePrefixNameStrategy();
        } else if (type == 2) {
            return new OnlySubFourTargetFilePrefixNameStrategy();
        }else if(type==3){
            return new PatternTargetFilePrefixNameStrategy();
        }
        return null;
    }
}
