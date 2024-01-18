package io.github.bigbird0101.code.core.template.variable.resource;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONObject;
import io.github.bigbird0101.spi.annotation.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class ConfigFileTemplateVariableResource extends AbstractNoShareVarTemplateVariableResource{
    private InputStream configFilePathStream;
    Map<String,Object> temp=new HashMap<>();
    public ConfigFileTemplateVariableResource() {
    }

    public ConfigFileTemplateVariableResource(InputStream configFilePathStream) {
        this.configFilePathStream = configFilePathStream;
    }
    @Inject
    public void setConfigFilePathStream(InputStream configFilePathStream) {
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

    @Override
    public String getType() {
        return "properties";
    }

}
