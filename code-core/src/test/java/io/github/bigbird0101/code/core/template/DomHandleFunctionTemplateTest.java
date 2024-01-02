package io.github.bigbird0101.code.core.template;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.URLUtil;
import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.template.domnode.DomScriptCodeNodeBuilderTest;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 17:37:11
 */
class DomHandleFunctionTemplateTest {

    @Test
    void test() throws SQLException {
        StandardEnvironment environment=new StandardEnvironment();
        environment.setTemplatesPath(URLUtil.decode(ResourceUtil.getResource("template").getPath()));
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        final Template dao = genericTemplateContext.getTemplate("testCodeNodeXml");
        Map<String, Object> dataModel = new HashMap<>(DomScriptCodeNodeBuilderTest.doBuildData(environment));
        final String templateResult = dao.process(dataModel).trim();
        assertNotNull(templateResult);
        final String domExceptResult = ResourceUtil.getResourceObj("DomExceptResult").readUtf8Str().trim().replaceAll("\r\n","\n");
        assertEquals(domExceptResult,templateResult);
    }
    @Test
    void test2() {
        StandardEnvironment environment=new StandardEnvironment();
        environment.setTemplatesPath(URLUtil.decode(ResourceUtil.getResource("template").getPath()));
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        final Template dao = genericTemplateContext.getTemplate("testCodeChooseNodeXml");
        Map<String, Object> dataModel = MapUtil.of("test","ad");
        String templateResult = dao.process(dataModel).trim();
        assertTrue(templateResult.length()>0);
        assertEquals("acelse",templateResult);
        dataModel = MapUtil.of("test","ac");
        templateResult = dao.process(dataModel).trim();
        assertTrue(templateResult.length()>0);
        assertEquals("ac",templateResult);
        dataModel = MapUtil.of("test","ab");
        templateResult = dao.process(dataModel).trim();
        assertTrue(templateResult.length()>0);
        assertEquals("ab\n" +
                "        test2ab",templateResult);
    }

    @Test
    void test3() {
        StandardEnvironment environment=new StandardEnvironment();
        environment.setTemplatesPath(URLUtil.decode(ResourceUtil.getResource("template").getPath()));
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        final Template dao = genericTemplateContext.getTemplate("testCodeChooseForeachNodeXml");
        Map<String, Object> dataModel = MapUtil.of("tableInfo2",MapUtil.of("columnList", ListUtil.of(MapUtil.of("test","ab2"))));
        String templateResult = dao.process(dataModel).trim();
        assertTrue(templateResult.length()>0);
        assertEquals("acelse",templateResult);
        dataModel = MapUtil.of("tableInfo2",MapUtil.of("columnList", ListUtil.of(MapUtil.of("test","ac"))));
        templateResult = dao.process(dataModel).trim();
        assertTrue(templateResult.length()>0);
        assertEquals("ac",templateResult);
        dataModel = MapUtil.of("tableInfo2",MapUtil.of("columnList", ListUtil.of(MapUtil.of("test","ab"))));
        templateResult = dao.process(dataModel).trim();
        assertTrue(templateResult.length()>0);
        assertEquals("ab\n" +
                "            test2ab",templateResult);
    }
}