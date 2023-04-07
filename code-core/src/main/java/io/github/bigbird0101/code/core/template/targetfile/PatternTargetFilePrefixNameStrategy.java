package io.github.bigbird0101.code.core.template.targetfile;

import io.github.bigbird0101.code.core.template.AbstractTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.core.template.TemplateResolver;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * @author Administrator
 */
public class PatternTargetFilePrefixNameStrategy implements TargetFilePrefixNameStrategy {
    private final Logger logger = LogManager.getLogger(getClass());

    private static final int TYPE_VALUE=3;

    private String pattern;
    /**
     * 获取命名策略代表值
     *
     * @return
     */
    @Override
    public int getTypeValue() {
        return TYPE_VALUE;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * 命名策略
     *
     * @param template  模板
     * @param srcSource 源资源 比如表名
     * @return
     */
    @Override
    public String prefixStrategy(Template template, String srcSource,Map<String,Object> dataModel) {
        AbstractTemplate abstractTemplate= (AbstractTemplate) template;
        TemplateResolver templateResolver = abstractTemplate.getTemplateResolver();
        String resolver = null;
        try {
            resolver = templateResolver.resolver(getPattern(), dataModel);
        } catch (TemplateResolveException e) {
            logger.error(e);
        }
        return resolver;
    }
}
