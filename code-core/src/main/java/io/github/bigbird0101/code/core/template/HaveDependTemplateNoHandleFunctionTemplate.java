package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.annotation.JSONType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

import static io.github.bigbird0101.code.core.template.DependTemplateResolver.TEMPLATE_FUNCTION_BODY_PATTERN;

/**
 * @author Administrator
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class HaveDependTemplateNoHandleFunctionTemplate extends DefaultNoHandleFunctionTemplate  implements HaveDependTemplate{
    private static final Logger LOGGER = LogManager.getLogger(HaveDependTemplateHandleFunctionTemplate.class);

    private Set<String> dependTemplates;

    @Override
    public Set<String> getDependTemplates() {
        return dependTemplates;
    }

    public void setDependTemplates(Set<String> dependTemplates) {
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
