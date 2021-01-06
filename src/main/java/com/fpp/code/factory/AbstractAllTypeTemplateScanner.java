package com.fpp.code.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

public abstract class AbstractAllTypeTemplateScanner implements AllTypeTemplateScanner {
    public JSONObject getConfigContent(String templatesFilePath) throws IOException {
        String result = IOUtils.toString(new FileInputStream(templatesFilePath));
        return (JSONObject) JSON.parse(result);
    }

    @Override
    public Set<MultipleTemplateDefinitionHolder> scannerMultiple(String templatesFilePath) throws IOException {
        JSONObject configContent = getConfigContent(templatesFilePath);
        JSONArray multipleTemplates = configContent.getJSONArray("MultipleTemplates");
        return get;
    }
}
