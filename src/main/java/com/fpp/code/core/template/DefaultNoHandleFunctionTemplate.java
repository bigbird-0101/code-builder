package com.fpp.code.core.template;

import com.alibaba.fastjson.annotation.JSONType;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.Environment;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/5/19 11:14
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DefaultNoHandleFunctionTemplate extends AbstractNoHandleFunctionTemplate {

    public DefaultNoHandleFunctionTemplate() throws CodeConfigException {
        super(null);
    }

    public DefaultNoHandleFunctionTemplate(String templateName, String templeFileName, Environment environment, String path) throws CodeConfigException {
        super(templeFileName,environment);
        this.setTemplateName(templateName);
        this.setPath(path);
    }


    public DefaultNoHandleFunctionTemplate(String templateName, String templeFileName, String path) throws CodeConfigException {
        super(templeFileName);
        this.setTemplateName(templateName);
        this.setPath(path);
    }

}
