package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.annotation.JSONType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;

import static io.github.bigbird0101.code.core.template.AbstractHandleTemplateResolver.TEMPLATE_FUNCTION_BODY_PATTERN;
import static io.github.bigbird0101.code.core.template.resolver.DependTemplateLangResolver.DEPEND_TEMPLATE_PATTERN;

/**
 * 有依赖模板的项目模板
 * @author Administrator
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class HaveDependTemplateHandleFunctionTemplate extends DefaultHandleFunctionTemplate implements HaveDependTemplate{
    private static final Logger LOGGER = LogManager.getLogger(HaveDependTemplateHandleFunctionTemplate.class);

    private LinkedHashSet<String> dependTemplates;

    @Override
    public LinkedHashSet<String> getDependTemplates() {
        return dependTemplates;
    }

    public void setDependTemplates(LinkedHashSet<String> dependTemplates) {
        this.dependTemplates = dependTemplates;
    }

    public HaveDependTemplateHandleFunctionTemplate(){
    }

    @Override
    public boolean doMatch(String content) {
        return ReUtil.isMatch(TEMPLATE_FUNCTION_BODY_PATTERN, content) && ReUtil.isMatch(DEPEND_TEMPLATE_PATTERN,content);
    }
}
