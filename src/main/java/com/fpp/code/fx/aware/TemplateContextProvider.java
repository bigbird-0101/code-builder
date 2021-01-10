package com.fpp.code.fx.aware;

import com.fpp.code.core.context.TemplateContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/1/8 9:35
 */
public abstract class TemplateContextProvider {
    private Logger logger= LogManager.getLogger("Controller");

    private static TemplateContext templateContext;

    public static void setTemplateContext(TemplateContext templateContext) {
        TemplateContextProvider.templateContext = templateContext;
    }

    public TemplateContext getTemplateContext() {
        return templateContext;
    }
}