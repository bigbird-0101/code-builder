package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.annotation.JSONType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;

import static io.github.bigbird0101.code.core.template.DefaultHandleFunctionTemplate.DOM_MATCH_RULE;
import static io.github.bigbird0101.code.core.template.DefaultHandleFunctionTemplate.DOM_MATCH_RULE_TEMPLATE;
import static io.github.bigbird0101.code.core.template.resolver.DependTemplateResolver.DEPEND_TEMPLATE_PATTERN;

/**
 * @author Administrator
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class HaveDependTemplateDomNoHandleFunctionTemplate extends DomNoHandleFunctionTemplate implements HaveDependTemplate{
    private static final Logger LOGGER = LogManager.getLogger(HaveDependTemplateHandleFunctionTemplate.class);

    private LinkedHashSet<String> dependTemplates;

    @Override
    public LinkedHashSet<String> getDependTemplates() {
        return dependTemplates;
    }

    public void setDependTemplates(LinkedHashSet<String> dependTemplates) {
        this.dependTemplates = dependTemplates;
    }

    @Override
    public boolean doMatch(String content) {
        return ReUtil.isMatch(DEPEND_TEMPLATE_PATTERN,content)&&
                ReUtil.isMatch(DOM_MATCH_RULE_TEMPLATE, content)
                &&!ReUtil.isMatch(DOM_MATCH_RULE, content);
    }

    @Override
    public int getOrder() {
        return 490;
    }
}
