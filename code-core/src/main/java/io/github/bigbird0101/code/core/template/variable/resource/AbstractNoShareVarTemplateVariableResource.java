package io.github.bigbird0101.code.core.template.variable.resource;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

import static java.util.stream.Collectors.toList;

public abstract class AbstractNoShareVarTemplateVariableResource implements TemplateVariableResource {
    /**
     * 非共享变量的前缀 必须是数组不是数组就是一个普通的变量 可以是json数组和其他类型的数组  如果有多个变量则以最短的长度为准
     * 例如
     *     配置文件当中使用 NO_SHARE_VAR_method=[{"a":"b"},{"a":"b1"}]
     *     那么在模板当中可以直接使用 *{method.a}* 区分大小写
     *
     */
    public static final String NO_SHARE_VAR_TEMPLATE_PREFIX="NO_SHARE_VAR_";

    public Queue<Map<String,Object>> getNoShareVar(){
        Queue<Map<String,Object>> linked=new LinkedList<>();
        Map<String, List<JSONObject>> map=new HashMap<>();
        /**
         * 获取文件当中的非共享变量
         * NO_SHARE_VAR_method=[{"a":"b"},{"a":"b1"}]
         * NO_SHARE_VAR_method2=[{"a":"b"},{"a":"b1"},{"a":"b2"}]
         * 将得到
         *  method=>[{"a":"b"},{"a":"b1"}]
         *  method2=>[{"a":"b"},{"a":"b1"},{"a":"b2"}]
         */
        MapUtil.filter(getTemplateVariable(), entry->entry.getKey().startsWith(NO_SHARE_VAR_TEMPLATE_PREFIX))
                .forEach((k,v)->{
                    if(v instanceof JSONArray){
                        JSONArray jsonArray= (JSONArray) v;
                        final List<JSONObject> collect = jsonArray.stream().map(s -> (JSONObject) s).collect(toList());
                        map.put(StrUtil.subSuf(k, NO_SHARE_VAR_TEMPLATE_PREFIX.length()), collect);
                    }
                });
        //得到值当中最短的
        int minSize=Integer.MAX_VALUE;
        for(Map.Entry<String,List<JSONObject>> entry:map.entrySet()){
            final List<JSONObject> value = entry.getValue();
            minSize=Math.min(minSize,value.size());
        }
        /**
         * 队列当中将得到这种结构 一行为队列当中的一个元素
         * method=>{"a":"b"},method2=>{"a":"b"}
         * method=>{"a":"b1"},method2=>{"a":"b1"}
         */
        for(int a=0;a<minSize;a++) {
            Map<String,Object> mapValue=new HashMap<>();
            for(Map.Entry<String,List<JSONObject>> entry:map.entrySet()){
                final String key = entry.getKey();
                final List<JSONObject> value = entry.getValue();
                mapValue.put(key,value.get(a));
            }
            linked.add(mapValue);
        }
        return linked;
    }
}
