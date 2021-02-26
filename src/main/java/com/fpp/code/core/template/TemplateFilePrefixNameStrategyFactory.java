package com.fpp.code.core.template;

public class TemplateFilePrefixNameStrategyFactory {

    public TemplateFilePrefixNameStrategy getTemplateFilePrefixNameStrategy(int type){
        if (type == 1) {
            return new DefaultTemplateFilePrefixNameStrategy();
        } else if (type == 2) {
            return new OnlySubFourTemplateFilePrefixNameStrategy();
        }else if(type==3){
            return new PatternTemplateFilePrefixNameStrategy();
        }
        return null;
    }
}
