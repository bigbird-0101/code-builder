package io.github.bigbird0101.code.core.template.domnode;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSONObject;
import io.github.bigbird0101.code.core.config.StandardEnvironment;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 13:43:04
 */
public class DomScriptCodeNodeBuilderTest2 {

    @Test
    void parse() throws SQLException {
        final DynamicCodeNodeContext dynamicCodeNodeContext = getDynamicCodeNodeContext();
        final Document document = XmlUtil.readXML(ResourceUtil.getStream("template/testCodeNodeXml.template"));
        DomScriptCodeNodeBuilder domScriptCodeNodeBuilder=new DomScriptCodeNodeBuilder(document);
        final CodeNode parse = domScriptCodeNodeBuilder.parse();
        parse.apply(dynamicCodeNodeContext);
        System.out.println(dynamicCodeNodeContext.getCode());
    }

    private static DynamicCodeNodeContext getDynamicCodeNodeContext() throws SQLException {
        StandardEnvironment standardEnvironment=new StandardEnvironment();
        standardEnvironment.parse();
        Map<String, Object> map = doBuildData(standardEnvironment);
        return new DynamicCodeNodeContext(map,standardEnvironment);
    }

    public static Map<String, Object> doBuildData(StandardEnvironment standardEnvironment) throws SQLException {
        JSONObject jsonObject=new JSONObject();
        final ArrayList<Object> objects = new ArrayList<>();
        JSONObject jsonObject1=new JSONObject();
        JSONObject jsonObject2=new JSONObject();
        jsonObject1.put("ab","test1123");
        jsonObject1.put("name","test1123");
        JSONObject jsonObject11=new JSONObject();
        JSONObject jsonObject12=new JSONObject();
        jsonObject11.put("testa","test11a");
        jsonObject11.put("testb","test11b");
        jsonObject11.put("name","test11a");
        jsonObject12.put("testa","test12a");
        jsonObject12.put("name","test12a");
        jsonObject12.put("testb","test12b");
        jsonObject1.put("list", Stream.of(jsonObject11,jsonObject12).collect(Collectors.toList()));

        JSONObject jsonObject21=new JSONObject();
        JSONObject jsonObject22=new JSONObject();
        jsonObject21.put("testa","test21a");
        jsonObject21.put("name","test21a");
        jsonObject21.put("testb","test21b");
        jsonObject22.put("testa","test22a");
        jsonObject22.put("name","test22a");
        jsonObject22.put("testb","test22b");
        jsonObject2.put("ab","test2123");
        jsonObject2.put("name","test2123");
        jsonObject2.put("list", Stream.of(jsonObject21,jsonObject22).collect(Collectors.toList()));

        objects.add(jsonObject1);
        objects.add(jsonObject2);
        jsonObject.put("columnList",objects);
        jsonObject.put("domainName","TestA");
        jsonObject.put("tableComment","TestA");
        Map<String,Object> map=new HashMap<>();
        map.put("tableInfo2", jsonObject);
        map.put("test","ab");
        map.put("test2","ab");
//        map.put("tableInfo", DbUtil.getTableInfo(DataSourceConfig.getDataSourceConfig(standardEnvironment),"tab_test", standardEnvironment));
        return map;
    }
}