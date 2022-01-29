package com.fpp.code.core;

import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.template.DefaultNoHandleFunctionTemplate;
import com.fpp.code.core.template.HaveDependTemplateHandleFunctionTemplate;
import com.fpp.code.core.template.Template;

import java.io.IOException;

public class Test {
    @org.junit.Test
    public void test() throws IOException, CodeConfigException {
        Template handleFunctionTemplate = new DefaultNoHandleFunctionTemplate();
        if(handleFunctionTemplate instanceof HaveDependTemplateHandleFunctionTemplate){
            System.out.println(handleFunctionTemplate);
        }
    }
}
