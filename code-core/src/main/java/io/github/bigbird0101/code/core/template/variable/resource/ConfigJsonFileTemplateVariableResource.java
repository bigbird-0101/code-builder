package io.github.bigbird0101.code.core.template.variable.resource;

import cn.hutool.core.map.MapUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件模板变量资源
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-24 23:35:56
 */
public class ConfigJsonFileTemplateVariableResource extends AbstractNoShareVarTemplateVariableResource{
    private InputStream configFilePathStream;

    Map<String,Object> temp=new HashMap<>();

    public ConfigJsonFileTemplateVariableResource() {
        this(null);
    }

    public ConfigJsonFileTemplateVariableResource(InputStream configFilePathStream) {
        this.configFilePathStream = configFilePathStream;
    }

    @Override
    public Map<String, Object> getTemplateVariable() {
        if(!temp.isEmpty()){
            StaticLog.debug("getTemplateVariable get cache");
            return temp;
        }
        try {
            Map<String,Object> object = JSONObject.<JSONObject>parseObject(configFilePathStream, StandardCharsets.UTF_8,
                    new TypeReference<Map<String,Object>>(){}.getType());
            temp.putAll(object);
            return temp;
        } catch (IOException e) {
            return MapUtil.empty();
        }
    }

    @Override
    public String getType() {
        return "json";
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public void init(Properties properties) {
        if(null==configFilePathStream) {
            configFilePathStream= (InputStream) properties.get(TemplateVariableResource.FILE_INPUT_STREAM);
        }
    }
}
