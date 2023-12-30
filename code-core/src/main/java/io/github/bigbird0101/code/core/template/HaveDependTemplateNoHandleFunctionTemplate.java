package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.annotation.JSONType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;

import static io.github.bigbird0101.code.core.template.resolver.DependTemplateResolver.TEMPLATE_FUNCTION_BODY_PATTERN;

/**
 * @author Administrator
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class HaveDependTemplateNoHandleFunctionTemplate extends DefaultNoHandleFunctionTemplate  implements HaveDependTemplate{
    private static final Logger LOGGER = LogManager.getLogger(HaveDependTemplateHandleFunctionTemplate.class);

    private LinkedHashSet<DependTemplate> dependTemplates;

    @Override
    public LinkedHashSet<DependTemplate> getDependTemplates() {
        return dependTemplates;
    }

    public void setDependTemplates(LinkedHashSet<DependTemplate> dependTemplates) {
        this.dependTemplates = dependTemplates;
    }

    @Override
    public boolean doMatch(String content) {
        return ReUtil.isMatch(TEMPLATE_FUNCTION_BODY_PATTERN,content);
    }

    @Override
    public int getOrder() {
        return 2000;
    }
}
