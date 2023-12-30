package io.github.bigbird0101.code.core;

import io.github.bigbird0101.code.core.cache.CacheKey;
import io.github.bigbird0101.code.core.template.DefaultHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.DefaultNoHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.GenericMultipleTemplate;
import io.github.bigbird0101.code.core.template.HaveDependTemplateHandleFunctionTemplate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateCopyTest {
    @Test
    @Disabled
    public void testTemplateCopyTest() {
        HaveDependTemplateHandleFunctionTemplate haveDependTemplateHandleFunctionTemplate=new HaveDependTemplateHandleFunctionTemplate();
        haveDependTemplateHandleFunctionTemplate.setProjectUrl("123");
        haveDependTemplateHandleFunctionTemplate.setDependTemplates(Stream.of("123", "1234").collect(Collectors.toCollection(LinkedHashSet::new)));
        HaveDependTemplateHandleFunctionTemplate clone =(HaveDependTemplateHandleFunctionTemplate)haveDependTemplateHandleFunctionTemplate.clone();
        System.out.println(haveDependTemplateHandleFunctionTemplate);
        System.out.println(clone);
        System.out.println(haveDependTemplateHandleFunctionTemplate.getDependTemplates().hashCode());
        System.out.println(clone.getDependTemplates().hashCode());
    }

    @Test
    @Disabled
    public void testMultipleTemplateCopyTest(){
        GenericMultipleTemplate genericMultipleTemplate=new GenericMultipleTemplate();
        genericMultipleTemplate.setTemplates(Stream.of(new DefaultHandleFunctionTemplate(),new DefaultNoHandleFunctionTemplate()).collect(Collectors.toSet()));
        genericMultipleTemplate.setTemplateName("123123");
        GenericMultipleTemplate clone = (GenericMultipleTemplate) genericMultipleTemplate.clone();
        System.out.println(genericMultipleTemplate);
        System.out.println(clone);
    }

    @Test
    public void testCache(){
        HashMap<String,Object> hashMap=new HashMap<>();
        CacheKey cacheKey=new CacheKey("你好",hashMap);
        CacheKey cacheKey2=new CacheKey("你好",hashMap);
        System.out.println(cacheKey.equals(cacheKey2));
        System.out.println(cacheKey.hashCode());
        System.out.println(cacheKey2.hashCode());
    }

    @RepeatedTest(15)
    public void test(){
        LinkedHashSet<String> collect = Stream.of("你看好,中国,大国1,大国2".split(","))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        System.out.println(collect);
    }

}
