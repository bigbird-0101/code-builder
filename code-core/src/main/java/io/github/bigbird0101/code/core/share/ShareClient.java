package io.github.bigbird0101.code.core.share;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import io.github.bigbird0101.code.core.exception.NoSuchTemplateDefinitionException;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;

import static cn.hutool.http.HttpStatus.HTTP_NOT_FOUND;

/**
 * @author bigbird-0101
 * @date 2024-06-09 22:53
 */
public class ShareClient {
    public MultipleTemplate multipleTemplate(String url){
        HttpResponse response = HttpUtil.createGet(url)
                .execute();
        byte[] input = response.bodyBytes();
        JSONObject jsonObject = (JSONObject) JSONObject.parse(input);
        MultipleTemplateDefinition o = JSONObject.parseObject(input, MultipleTemplateDefinition.class);
        JSONObject templateMaps = jsonObject.getJSONObject("templateMaps");
        return null;
    }

    public Template template(String url){
        HttpResponse response = HttpUtil.createGet(url)
                .execute();
        byte[] input = response.bodyBytes();
        JSONObject jsonObject = (JSONObject) JSONObject.parse(input);
        TemplateDefinition o = JSONObject.parseObject(input, TemplateDefinition.class);
        return null;
    }

    public String templateContent(String url){
        HttpResponse response = HttpUtil.createGet(url)
                .execute();
        if(response.isOk()) {
            byte[] input = response.bodyBytes();
            JSONObject jsonObject = (JSONObject) JSONObject.parse(input);
            return jsonObject.getString("templateContent");
        }else{
            if(response.getStatus()== HTTP_NOT_FOUND){
                throw new NoSuchTemplateDefinitionException(response.body());
            }else{
                return null;
            }
        }
    }
}
