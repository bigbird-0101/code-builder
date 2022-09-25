package com.fpp.code.core.template.variable.resource;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 配置文件模板变量资源
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-24 23:35:56
 */
public class ConfigFileTemplateVariableResource implements TemplateVariableResource{
    /**
     * 非共享变量的前缀 必须是数组不是数组就是一个普通的变量 可以是json数组和其他类型的数组
     * 例如
     *     配置文件当中使用 NO_SHARE_VAR_method=[{"a":"b"},{"a":"b1"}]
     *     那么在模板当中可以直接使用 *{method.a}* 区分大小写
     *
     */
    public static final String NO_SHARE_VAR_TEMPLATE_PREFIX="NO_SHARE_VAR_";
    private final InputStream configFilePathStream;

    Map<String,Object> temp=new HashMap<>();


    public ConfigFileTemplateVariableResource(InputStream configFilePathStream) {
        this.configFilePathStream = configFilePathStream;
    }

    @Override
    public Map<String, Object> getTemplateVariable() {
        if(!temp.isEmpty()){
            StaticLog.debug("getTemplateVariable get cache");
            return temp;
        }
        final Properties properties = new Properties();
        try (InputStreamReader fileInputStream = new InputStreamReader(configFilePathStream, StandardCharsets.UTF_8)) {
            properties.load(fileInputStream);
            properties.forEach((k, v) -> {
                String value= (String) v;
                if(JSONUtil.isTypeJSON(value)){
                    temp.put((String) k, JSONObject.parse(value));
                }else {
                    temp.put((String) k, value);
                }
            });
            return temp;
        } catch (IOException e) {
            return MapUtil.empty();
        }
    }

    public Queue<Map<String,Object>> getNoShareVar(){
        Queue<Map<String,Object>> linked=new LinkedList<>();
        MapUtil.filter(getTemplateVariable(),entry->entry.getKey().startsWith(NO_SHARE_VAR_TEMPLATE_PREFIX))
                .forEach((k,v)->{
                    if(v instanceof JSONArray){
                        JSONArray jsonArray= (JSONArray) v;
                        jsonArray.forEach(entry->{
                            linked.add(MapUtil.of(StrUtil.subSuf(k,NO_SHARE_VAR_TEMPLATE_PREFIX.length()),entry));
                        });
                    }
                });
        return linked;
    }
}
