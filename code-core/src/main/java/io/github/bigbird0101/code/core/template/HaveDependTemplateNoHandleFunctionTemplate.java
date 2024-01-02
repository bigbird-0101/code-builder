package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.annotation.JSONType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;

import static io.github.bigbird0101.code.core.template.resolver.DependTemplateLangResolver.DEPEND_TEMPLATE_PATTERN;

/**
 * @author Administrator
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class HaveDependTemplateNoHandleFunctionTemplate extends DefaultNoHandleFunctionTemplate  implements HaveDependTemplate{
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
        return ReUtil.isMatch(DEPEND_TEMPLATE_PATTERN,content);
    }

    @Override
    public int getOrder() {
        return 2000;
    }
}
