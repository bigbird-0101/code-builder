package io.github.bigbird0101.code.core.template.domnode;

import cn.hutool.core.io.resource.ResourceUtil;
import io.github.bigbird0101.code.core.DataModelProvider;
import io.github.bigbird0101.code.core.ExceptResultProvider;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.template.DomHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.Template;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IfForeachNodeTest {

    @Test
    void testIfForeach() {
        GenericTemplateContext genericTemplateContext = new GenericTemplateContext();
        Template template = genericTemplateContext.getTemplate("testIfForeachNodeXml");
        Map<String, Object> dataModel = DataModelProvider.getDataModel();
        dataModel.put("respVoSimpleName", "UserRespVo");
        dataModel.put("doSimpleName", "User");
        dataModel.put("firstLowerDaoSimpleName", "UserDao");
        String process = template.process(dataModel);
        assertEquals(ExceptResultProvider.getExceptResult("testIfForeachNodeXml"), process);
    }

    @Test
    void testIfForeach2() {
        URL resource = ResourceUtil.getResource("META-INF/templates/testIfForeachNodeXml.template");
        DomHandleFunctionTemplate domHandleFunctionTemplate = new DomHandleFunctionTemplate(resource);
        Map<String, Object> dataModel = DataModelProvider.getDataModel();
        dataModel.put("respVoSimpleName", "UserRespVo");
        dataModel.put("doSimpleName", "User");
        dataModel.put("firstLowerDaoSimpleName", "UserDao");
        String process = domHandleFunctionTemplate.process(dataModel);
        assertEquals(ExceptResultProvider.getExceptResult("testIfForeachNodeXml"), process);
    }
}
