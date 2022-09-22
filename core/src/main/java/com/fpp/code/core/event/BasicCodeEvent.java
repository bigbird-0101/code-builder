package com.fpp.code.core.event;

import java.util.EventObject;

/**
 * 基础代码事件
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-20 23:01:44
 */
public abstract class BasicCodeEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public BasicCodeEvent(Object source) {
        super(source);
    }
}
