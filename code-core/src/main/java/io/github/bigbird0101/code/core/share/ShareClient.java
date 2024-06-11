package io.github.bigbird0101.code.core.share;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import io.github.bigbird0101.code.core.config.FileUrlResource;
import io.github.bigbird0101.code.core.context.aware.TemplateContextProvider;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.exception.NoSuchTemplateDefinitionException;
import io.github.bigbird0101.code.core.factory.GenericTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.template.Template;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hutool.http.HttpStatus.HTTP_NOT_FOUND;
import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH;
import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_TEMPLATE_FILE_SUFFIX;

/**
 * @author bigbird-0101
 * @date 2024-06-09 22:53
 */
public class ShareClient extends TemplateContextProvider {

    public static final String TEMPLATE_NAME = "templateName";
    public static final String TEMPLATE_MAPS = "templateMaps";
    public static final String DEPEND_TEMPLATE_MAPS = "dependTemplateMaps";
    public static final String TEMPLATE_CONTENT = "templateContent";

    public MultipleTemplateDefinition multipleTemplate(String url) {
        HttpResponse response = HttpUtil.createGet(url)
                .execute();
        byte[] input = response.bodyBytes();
        JSONObject jsonObject = (JSONObject) JSONObject.parse(input);
        MultipleTemplateDefinition o = JSONObject.parseObject(input, MultipleTemplateDefinition.class);
        JSONObject templateMaps = jsonObject.getJSONObject(TEMPLATE_MAPS);
        return null;
    }

    public TemplateDefinitionWrapper template(String url) {
        HttpResponse response = HttpUtil.createGet(url)
                .execute();
        byte[] input = response.bodyBytes();
        JSONObject jsonObject = (JSONObject) JSONObject.parse(input);
        GenericTemplateDefinition o = getGenericTemplateDefinition(jsonObject);
        List<TemplateDefinitionWrapper> collect = getDependTemplateDefinitionWrappers(jsonObject);
        return new TemplateDefinitionWrapper(o, jsonObject.getString(TEMPLATE_NAME), collect);
    }

    private List<TemplateDefinitionWrapper> getDependTemplateDefinitionWrappers(JSONObject jsonObject) {
        JSONObject templateMaps = jsonObject.getJSONObject(DEPEND_TEMPLATE_MAPS);
        if (CollUtil.isEmpty(templateMaps)) {
            return Collections.emptyList();
        }
        return templateMaps.values().stream().map(object -> {
            JSONObject dependObj = (JSONObject) object;
            GenericTemplateDefinition genericTemplateDefinition = getGenericTemplateDefinition(dependObj);
            return new TemplateDefinitionWrapper(genericTemplateDefinition, dependObj.getString(TEMPLATE_NAME),
                    getDependTemplateDefinitionWrappers(dependObj));
        }).collect(Collectors.toList());
    }

    private GenericTemplateDefinition getGenericTemplateDefinition(JSONObject jsonObject) {
        GenericTemplateDefinition o = JSONObject.parseObject(jsonObject.toJSONString(), GenericTemplateDefinition.class);
        String templateName = jsonObject.getString(TEMPLATE_NAME);
        try {
            Template template = getTemplateContext().getTemplate(templateName);
            if (null != template) {
                templateName = templateName + "_share";
            }
        } catch (NoSuchTemplateDefinitionException ignore) {
        }
        File file = new File(getTemplateContext().getEnvironment()
                .getProperty(DEFAULT_CORE_TEMPLATE_FILES_PATH) + File.separator +
                templateName + DEFAULT_TEMPLATE_FILE_SUFFIX);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter.create(file).write(jsonObject.getString(TEMPLATE_CONTENT));
            o.setTemplateResource(new FileUrlResource(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new CodeConfigException(e);
        }
        return o;
    }

    public String templateContent(String url){
        HttpResponse response = HttpUtil.createGet(url)
                .execute();
        if(response.isOk()) {
            byte[] input = response.bodyBytes();
            JSONObject jsonObject = (JSONObject) JSONObject.parse(input);
            return jsonObject.getString(TEMPLATE_CONTENT);
        }else{
            if(response.getStatus()== HTTP_NOT_FOUND){
                throw new NoSuchTemplateDefinitionException(response.body());
            }else{
                return null;
            }
        }
    }
}
