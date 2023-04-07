package io.github.bigbird0101.code.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.factory.RootTemplateDefinition;
import io.github.bigbird0101.code.core.template.DefaultNoHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.HaveDependTemplateHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.Template;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test2 {
    @Test
    public void testSplitForeach(){
        String a="column in  interfaceModule.columnList";
        Stream.of(a.split(" in ")).forEach(System.out::println);
    }

    @Test
    public void test() throws CodeConfigException {
        Template handleFunctionTemplate = new DefaultNoHandleFunctionTemplate();
        if(handleFunctionTemplate instanceof HaveDependTemplateHandleFunctionTemplate){
            System.out.println(handleFunctionTemplate);
        }
    }

    @Test
    public void testCopyProperties(){
        Template template=new HaveDependTemplateHandleFunctionTemplate();
        RootTemplateDefinition templateDefinition=new RootTemplateDefinition();
        templateDefinition.setTemplateClassName("io.github.bigbird0101.code.core.template.DefaultHandleFunctionTemplate");
        templateDefinition.setProjectUrl("123123");
        templateDefinition.setModule("ddddd");
        templateDefinition.setTargetFileSuffixName("java");
        templateDefinition.setDependTemplates(Stream.of("123","1232").collect(Collectors.toCollection(LinkedHashSet::new)));
        BeanUtil.copyProperties(templateDefinition,template);
        System.out.println(template);
    }

    @Test
    public void testDefinition(){
        String s="{\n" +
                "\"fileName\":\"DaoFileTemplate_04.template\",\n" +
                "\"isHandleFunction\":\"1\",\n"+
                "\"projectUrl\":\"C:\\\\code\\\\ideaWorkSpace\\\\untitled1\\\\out\",\n" +
                "\"sourcesRoot\":\"123\",\n" +
                "\"fileSuffixName\":\"123\",\n" +
                "\"module\":\"123\",\n" +
                "\"name\":\"test23\",\n" +
                "\"dependTemplates\":[\n" +
                "\t\"doman模板\"\n" +
                "],\n" +
                "\"srcPackage\":\"123\",\n" +
                "\"templateClassName\":\"io.github.bigbird0101.code.core.template.HaveDependTemplateHandleFunctionTemplate\",\n" +
                "\"filePrefixNameStrategy\":{\"value\":3,\"pattern\":\"*{tableInfo.domainName}*RpcImplService222\"}\n" +
                "}";
        System.out.println(s);
        RootTemplateDefinition rootTemplateDefinition = JSONObject.parseObject(s, RootTemplateDefinition.class);
        System.out.println(rootTemplateDefinition);
    }

    @Test
    public void test3(){
        System.out.println(StrUtil.isBlank(" "));
    }
}
