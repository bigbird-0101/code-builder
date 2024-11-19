package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.annotation.JSONType;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.exception.CodeConfigException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import static io.github.bigbird0101.code.core.template.AbstractHandleAbstractTemplateResolver.FUNCTION_BODY_BETWEEN_SPLIT;
import static io.github.bigbird0101.code.core.template.AbstractHandleAbstractTemplateResolver.TEMPLATE_FUNCTION_BODY_PATTERN;
import static io.github.bigbird0101.code.core.template.domnode.DomScriptCodeNodeBuilder.CodeNodeHandler.TEMPLATE;

/**
 * @author fpp
 * @version 1.0
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DefaultHandleFunctionTemplate extends AbstractHandleFunctionTemplate{
    protected static final Pattern DOM_MATCH_RULE = Pattern.compile("(?<title>.*?)(\\<\\s*?/" + FUNCTION_BODY_BETWEEN_SPLIT + "\\s*?\\>)(?<title2>.*?)", Pattern.DOTALL);
    protected static final Pattern DOM_MATCH_RULE_TEMPLATE = Pattern.compile("(\\<\\s*?" + TEMPLATE + "\\s*?\\>)(?<title>.*?)(\\<\\s*?/" + TEMPLATE + "\\s*?\\>)", Pattern.DOTALL);

    public DefaultHandleFunctionTemplate() {
    }

    public DefaultHandleFunctionTemplate(String templeFileName) throws CodeConfigException {
        super(templeFileName);
    }

    public DefaultHandleFunctionTemplate(String templeFileName, Environment environment) throws CodeConfigException {
        super(templeFileName, environment);
    }

    public DefaultHandleFunctionTemplate(String templeFileName, TemplateResolver templateResolver) {
        super(templeFileName, templateResolver);
    }

    public DefaultHandleFunctionTemplate(String templateName, URL templateFileUrl) {
        super(templateName, templateFileUrl);
    }

    public DefaultHandleFunctionTemplate(URL templateFileUrl) {
        super(templateFileUrl);
    }

    public DefaultHandleFunctionTemplate(File file) throws IOException {
        super(file);
    }

    /**
     * 设置模板解析策略
     *
     * @param resolverStrategy 模板解析策略
     */
    @Override
    public void setResolverStrategy(ResolverStrategy resolverStrategy) {
        this.resolverStrategy=resolverStrategy;
    }

    @Override
    public boolean doMatch(String content) {
        return ReUtil.isMatch(TEMPLATE_FUNCTION_BODY_PATTERN, content);
    }

    @Override
    public int getOrder() {
        return 1000;
    }
}
