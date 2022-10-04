package com.fpp.code.core.template;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.annotation.JSONType;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.config.aware.EnvironmentAware;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.template.languagenode.CodeNode;
import com.fpp.code.core.template.languagenode.DomScriptCodeNodeBuilder;
import com.fpp.code.core.template.languagenode.DynamicCodeNodeContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 20:20:57
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DomNoHandleFunctionTemplate extends DefaultNoHandleFunctionTemplate implements EnvironmentAware {
    public DomNoHandleFunctionTemplate() {
        this.setTemplateResolver(new SimpleTemplateResolver());
    }
    private CodeNode source;
    private Environment environment;
    @Override
    public void refresh() {
        if(null!=getTemplateFile()) {
            try {
                final FileInputStream fileInputStream = new FileInputStream(getTemplateFile());
                DomScriptCodeNodeBuilder domScriptCodeNodeBuilder=new DomScriptCodeNodeBuilder(XmlUtil.readXML(fileInputStream));
                source= domScriptCodeNodeBuilder.parse();
            } catch (FileNotFoundException e) {
                throw new CodeConfigException(e);
            }
        }
    }
    @Override
    protected String doBuildTemplateResultCache() {
        DynamicCodeNodeContext dynamicCodeNodeContext=new DynamicCodeNodeContext(getTemplateVariables(),environment);
        source.apply(dynamicCodeNodeContext);
        return getTemplateResolver().resolver(dynamicCodeNodeContext.getCode(),getTemplateVariables());
    }

    @Override
    public void setEnvironment(Environment environment) {
       this.environment=environment;
    }
}
