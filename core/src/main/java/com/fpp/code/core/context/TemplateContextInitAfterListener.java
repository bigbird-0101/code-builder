package com.fpp.code.core.context;

import com.fpp.code.core.context.aware.TemplateContextProvider;
import com.fpp.code.core.event.TemplateContextListener;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-22 22:56:22
 */
public class TemplateContextInitAfterListener extends TemplateContextListener<TemplateContextInitAfterEvent> {
    @Override
    protected void onTemplateContextEvent(TemplateContextInitAfterEvent templateContextInitAfterEvent) {
        TemplateContextProvider.doPushEventTemplateContextAware();
    }
}
