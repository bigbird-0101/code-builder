package io.github.bigbird0101.code.core.template;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 模板交易容器 获取当前的模板
 * @author Administrator
 */
public class TemplateTraceContext {
    private static final Logger logger= LogManager.getLogger(TemplateTraceContext.class);

    private static final ThreadLocal<Template> CURRENT_TEMPLATE=new ThreadLocal<>();

    public static Template getCurrentTemplate() {
        return CURRENT_TEMPLATE.get();
    }

    public static void setCurrentTemplate(Template currentTemplate) {
        logger.info("setCurrentTemplate {}",currentTemplate.getTemplateName());
        TemplateTraceContext.CURRENT_TEMPLATE.set(currentTemplate);
    }

    public static void clear(){
        CURRENT_TEMPLATE.remove();
    }
}
