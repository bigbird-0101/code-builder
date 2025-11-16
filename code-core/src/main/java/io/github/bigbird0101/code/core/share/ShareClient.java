package io.github.bigbird0101.code.core.share;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import io.github.bigbird0101.code.core.config.FileUrlResource;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.exception.NoSuchMultipleTemplateDefinitionException;
import io.github.bigbird0101.code.core.exception.NoSuchTemplateDefinitionException;
import io.github.bigbird0101.code.core.factory.GenericMultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.GenericTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hutool.http.HttpStatus.HTTP_NOT_FOUND;
import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH;
import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_TEMPLATE_FILE_SUFFIX;
import static io.github.bigbird0101.code.core.share.ShareConstant.MULTIPLE_TEMPLATE;
import static io.github.bigbird0101.code.core.share.ShareConstant.TEMPLATE;

/**
 * @author bigbird-0101
 * @date 2024-06-09 22:53
 */
public class ShareClient extends AbstractTemplateContextProvider {
    private static final Logger LOGGER = LogManager.getLogger(ShareClient.class);

    public boolean isTemplateShareUrl(String url) {
        return url.contains(TEMPLATE);
    }

    public boolean isMultipleTemplateShareUrl(String url) {
        return url.contains(MULTIPLE_TEMPLATE);
    }

    public MultipleTemplateDefinitionWrapper multipleTemplate(String url) {
        try (HttpResponse response = HttpUtil.createGet(UrlBuilder.of(url).build())
                .execute()) {
            if (response.isOk()) {
                byte[] input = response.bodyBytes();
                JSONObject jsonObject = (JSONObject) JSONObject.parse(input);
                MultipleTemplateDefinition multipleTemplateDefinition = JSONObject.parseObject(input, GenericMultipleTemplateDefinition.class);
                String multipleTemplateName = jsonObject.getString(ShareConstant.TEMPLATE_NAME);
                JSONObject templateMaps = jsonObject.getJSONObject(ShareConstant.TEMPLATE_MAPS);
                List<TemplateDefinitionWrapper> templateDefinitionWrappers = templateMaps
                        .values()
                        .stream()
                        .map(object -> {
                            JSONObject jsonObjectTemplate = (JSONObject) object;
                            GenericTemplateDefinition genericTemplateDefinition = getGenericTemplateDefinition(jsonObjectTemplate);
                            List<TemplateDefinitionWrapper> collect = getDependTemplateDefinitionWrappers(jsonObjectTemplate);
                            return new TemplateDefinitionWrapper(genericTemplateDefinition, jsonObjectTemplate.getString(ShareConstant.TEMPLATE_NAME), collect);
                        }).collect(Collectors.toList());
                return new MultipleTemplateDefinitionWrapper(multipleTemplateName, multipleTemplateDefinition, templateDefinitionWrappers);
            } else {
                if (response.getStatus() != HTTP_NOT_FOUND) {
                    LOGGER.error("require {} error {}", url, response.body());
                }
                throw new NoSuchMultipleTemplateDefinitionException(response.body());
            }
        }
    }

    public TemplateDefinitionWrapper template(String url) {
        try (HttpResponse response = HttpUtil.createGet(UrlBuilder.of(url).build())
                .execute()) {
            if (response.isOk()) {
                byte[] input = response.bodyBytes();
                JSONObject jsonObject = (JSONObject) JSONObject.parse(input);
                GenericTemplateDefinition o = getGenericTemplateDefinition(jsonObject);
                List<TemplateDefinitionWrapper> collect = getDependTemplateDefinitionWrappers(jsonObject);
                return new TemplateDefinitionWrapper(o, jsonObject.getString(ShareConstant.TEMPLATE_NAME), collect);
            } else {
                if (response.getStatus() != HTTP_NOT_FOUND) {
                    LOGGER.error("require {} error {}", url, response.body());
                }
                throw new NoSuchTemplateDefinitionException(response.body());
            }
        }
    }

    private List<TemplateDefinitionWrapper> getDependTemplateDefinitionWrappers(JSONObject jsonObject) {
        JSONObject templateMaps = jsonObject.getJSONObject(ShareConstant.DEPEND_TEMPLATE_MAPS);
        if (CollUtil.isEmpty(templateMaps)) {
            return Collections.emptyList();
        }
        return templateMaps.values().stream().map(object -> {
            JSONObject dependObj = (JSONObject) object;
            GenericTemplateDefinition genericTemplateDefinition = getGenericTemplateDefinition(dependObj);
            return new TemplateDefinitionWrapper(genericTemplateDefinition, dependObj.getString(ShareConstant.TEMPLATE_NAME),
                    getDependTemplateDefinitionWrappers(dependObj));
        }).collect(Collectors.toList());
    }

    private GenericTemplateDefinition getGenericTemplateDefinition(JSONObject jsonObject) {
        GenericTemplateDefinition o = JSONObject.parseObject(jsonObject.toJSONString(), GenericTemplateDefinition.class);
        String templateName = jsonObject.getString(ShareConstant.TEMPLATE_NAME);
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
            FileWriter.create(file).write(jsonObject.getString(ShareConstant.TEMPLATE_CONTENT));
            o.setTemplateResource(new FileUrlResource(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new CodeConfigException(e);
        }
        return o;
    }

    public String templateContent(String url){
        try (HttpResponse response = HttpUtil.createGet(UrlBuilder.of(url).build())
                .execute()) {
            if (response.isOk()) {
                byte[] input = response.bodyBytes();
                JSONObject jsonObject = (JSONObject) JSONObject.parse(input);
                return jsonObject.getString(ShareConstant.TEMPLATE_CONTENT);
            } else {
                if (response.getStatus() != HTTP_NOT_FOUND) {
                    LOGGER.error("require {} error {}", url, response.body());
                }
                throw new NoSuchTemplateDefinitionException(response.body());
            }
        }
    }
}
