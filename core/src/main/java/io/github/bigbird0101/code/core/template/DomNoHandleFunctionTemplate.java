package io.github.bigbird0101.code.core.template;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.annotation.JSONType;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.config.aware.EnvironmentAware;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.template.languagenode.CodeNode;
import io.github.bigbird0101.code.core.template.languagenode.DomScriptCodeNodeBuilder;
import io.github.bigbird0101.code.core.template.languagenode.DynamicCodeNodeContext;
import io.github.bigbird0101.code.exception.TemplateResolveException;

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
            } catch (UtilException ed) {
                throw new TemplateResolveException("dom template Resolve error {}",ed.getMessage());
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
