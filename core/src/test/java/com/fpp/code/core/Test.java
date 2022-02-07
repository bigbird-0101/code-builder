package com.fpp.code.core;

import com.fpp.code.core.common.ObjectUtils;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.factory.RootTemplateDefinition;
import com.fpp.code.core.template.DefaultNoHandleFunctionTemplate;
import com.fpp.code.core.template.HaveDependTemplateHandleFunctionTemplate;
import com.fpp.code.core.template.Template;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {
    @org.junit.Test
    public void test() throws IOException, CodeConfigException {
        Template handleFunctionTemplate = new DefaultNoHandleFunctionTemplate();
        if(handleFunctionTemplate instanceof HaveDependTemplateHandleFunctionTemplate){
            System.out.println(handleFunctionTemplate);
        }
    }

    @org.junit.Test
    public void testCopyProperties(){
        Template template=new HaveDependTemplateHandleFunctionTemplate();
        RootTemplateDefinition templateDefinition=new RootTemplateDefinition();
        templateDefinition.setTemplateClassName("com.fpp.code.core.template.DefaultHandleFunctionTemplate");
        templateDefinition.setProjectUrl("123123");
        templateDefinition.setModule("ddddd");
        templateDefinition.setTemplateFileSuffixName("java");
        templateDefinition.setDependTemplates(Stream.of("123","1232").collect(Collectors.toSet()));
        ObjectUtils.copyProperties(templateDefinition,template);
        System.out.println(template);
    }
}
