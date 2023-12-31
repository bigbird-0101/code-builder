package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.annotation.JSONType;

import java.util.regex.Pattern;

import static io.github.bigbird0101.code.core.template.AbstractTemplateResolver.FUNCTION_BODY_BETWEEN_SPLIT;
import static io.github.bigbird0101.code.core.template.AbstractTemplateResolver.templateFunctionBodyPattern;
import static io.github.bigbird0101.code.core.template.domnode.DomScriptCodeNodeBuilder.CodeNodeHandler.TEMPLATE;

/**
 * @author fpp
 * @version 1.0
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DefaultHandleFunctionTemplate extends AbstractHandleFunctionTemplate{
    protected static final Pattern DOM_MATCH_RULE = Pattern.compile("(?<title>.*?)(\\<\\s*?/" + FUNCTION_BODY_BETWEEN_SPLIT + "\\s*?\\>)(?<title2>.*?)", Pattern.DOTALL);
    protected static final Pattern DOM_MATCH_RULE_TEMPLATE = Pattern.compile("(\\<\\s*?" + TEMPLATE + "\\s*?\\>)(?<title>.*?)(\\<\\s*?/" + TEMPLATE + "\\s*?\\>)", Pattern.DOTALL);

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
        return ReUtil.isMatch(templateFunctionBodyPattern, content);
    }

    @Override
    public int getOrder() {
        return 1000;
    }
}
