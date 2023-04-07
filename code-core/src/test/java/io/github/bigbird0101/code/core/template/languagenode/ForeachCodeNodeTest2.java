package io.github.bigbird0101.code.core.template.languagenode;

import com.alibaba.fastjson.JSONObject;
import io.github.bigbird0101.code.core.config.StandardEnvironment;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-01 22:05:33
 */
class ForeachCodeNodeTest2 {

    @Test
    void apply() throws SQLException {
        StandardEnvironment standardEnvironment=new StandardEnvironment();
        standardEnvironment.parse();
        Map<String,Object> map=new HashMap<>();
        map.put("tableInfo", DomScriptCodeNodeBuilderTest2.doBuildData(null).get("tableInfo2"));
        DynamicCodeNodeContext dynamicCodeNodeContext=new DynamicCodeNodeContext(map,standardEnvironment);
        ForeachCodeNode foreachCodeNode=new ForeachCodeNode(new StaticTextCodeNode("*{column.name}*\n"),
                ",","tableInfo.columnList", "column");
        foreachCodeNode.apply(dynamicCodeNodeContext);
        System.out.println(dynamicCodeNodeContext.getCode());
    }

    @Test
    void apply2() {
        StandardEnvironment standardEnvironment=new StandardEnvironment();
        standardEnvironment.parse();
        JSONObject jsonObject=new JSONObject();
        final ArrayList<Object> objects = new ArrayList<>();
        JSONObject jsonObject1=new JSONObject();
        JSONObject jsonObject2=new JSONObject();
        jsonObject1.put("ab","test1123");
        JSONObject jsonObject11=new JSONObject();
        JSONObject jsonObject12=new JSONObject();
        jsonObject11.put("testa","test11a");
        jsonObject11.put("testb","test11b");
        jsonObject12.put("testa","test12a");
        jsonObject12.put("testb","test12b");
        jsonObject1.put("list", Stream.of(jsonObject11,jsonObject12).collect(Collectors.toList()));

        JSONObject jsonObject21=new JSONObject();
        JSONObject jsonObject22=new JSONObject();
        jsonObject21.put("testa","test21a");
        jsonObject21.put("testb","test21b");
        jsonObject22.put("testa","test22a");
        jsonObject22.put("testb","test22b");
        jsonObject2.put("ab","test2123");
        jsonObject2.put("list", Stream.of(jsonObject21,jsonObject22).collect(Collectors.toList()));

        objects.add(jsonObject1);
        objects.add(jsonObject2);
        jsonObject.put("columnList",objects);
        Map<String,Object> map=new HashMap<>();
        map.put("tableInfo",jsonObject);
        DynamicCodeNodeContext dynamicCodeNodeContext=new DynamicCodeNodeContext(map,standardEnvironment);
        final StaticTextCodeNode contents = new StaticTextCodeNode("这是上层循环*{column.ab}*\n");
        final StaticTextCodeNode contents2 = new StaticTextCodeNode("这是内层循环 *{list.testa}* *{list.testb}*\n");
        ForeachCodeNode foreachCodeNode2=new ForeachCodeNode(contents2,
                "","column.list", "list");
        final MixCodeNode mixCodeNode = new MixCodeNode(Arrays.asList(contents,foreachCodeNode2));
        ForeachCodeNode foreachCodeNode=new ForeachCodeNode(mixCodeNode,
                "","tableInfo.columnList", "column");
        foreachCodeNode.apply(dynamicCodeNodeContext);
        System.out.println(dynamicCodeNodeContext.getCode());

    }
}