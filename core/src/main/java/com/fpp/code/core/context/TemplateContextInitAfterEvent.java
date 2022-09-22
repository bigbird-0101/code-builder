package com.fpp.code.core.context;

import com.fpp.code.core.event.TemplateContextEvent;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-22 22:54:02
 */
public class TemplateContextInitAfterEvent extends TemplateContextEvent {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public TemplateContextInitAfterEvent(Object source) {
        super(source);
    }
}
