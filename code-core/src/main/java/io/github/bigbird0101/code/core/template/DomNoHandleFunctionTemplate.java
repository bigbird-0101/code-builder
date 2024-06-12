package io.github.bigbird0101.code.core.template;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.annotation.JSONType;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.config.aware.EnvironmentAware;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.template.domnode.CodeNode;
import io.github.bigbird0101.code.core.template.domnode.DomScriptCodeNodeBuilder;
import io.github.bigbird0101.code.core.template.domnode.DynamicCodeNodeContext;
import io.github.bigbird0101.code.exception.TemplateResolveException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static io.github.bigbird0101.code.core.template.DefaultHandleFunctionTemplate.DOM_MATCH_RULE;
import static io.github.bigbird0101.code.core.template.DefaultHandleFunctionTemplate.DOM_MATCH_RULE_TEMPLATE;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 20:20:57
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DomNoHandleFunctionTemplate extends DefaultNoHandleFunctionTemplate implements EnvironmentAware {
    public DomNoHandleFunctionTemplate() {
        this.setTemplateResolver(SimpleAbstractTemplateResolver.getInstance());
    }
    private CodeNode source;
    private Environment environment;
    @Override
    public void refresh() {
        if(null!= getTemplateResource()) {
            try {
                DomScriptCodeNodeBuilder domScriptCodeNodeBuilder=new DomScriptCodeNodeBuilder(XmlUtil.readXML(
                        getTemplateResource().getInputStream()));
                source= domScriptCodeNodeBuilder.parse();
            } catch (FileNotFoundException e) {
                throw new CodeConfigException(e);
            } catch (UtilException | IOException ed) {
                throw new TemplateResolveException("dom {} template Resolve error {}",getTemplateName(),ed.getMessage());
            }
        }
        refreshed=true;
    }

    @Override
    public boolean doMatch(String content) {
            return ReUtil.isMatch(DOM_MATCH_RULE_TEMPLATE, content)
                    &&!ReUtil.isMatch(DOM_MATCH_RULE, content);
    }

    @Override
    public int getOrder() {
        return 510;
    }

    @Override
    protected String doBuildTemplateResultCache(Map<String, Object> dataModel) {
        DynamicCodeNodeContext dynamicCodeNodeContext=new DynamicCodeNodeContext(dataModel,environment);
        source.apply(dynamicCodeNodeContext);
        return getTemplateResolver().resolver(dynamicCodeNodeContext.getCode(),dataModel);
    }

    @Override
    public void setEnvironment(Environment environment) {
       this.environment=environment;
    }
}
