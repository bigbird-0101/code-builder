package com.fpp.code.core.event;

import java.util.EventListener;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-20 23:04:42
 */
public interface BasicCodeListener<E extends BasicCodeEvent> extends EventListener {
    /**
     * 事件处理逻辑
     * @param e
     */
    void onBasicCodeEvent(E e);
}
