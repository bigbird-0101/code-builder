package com.fpp.code.core.event;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-20 22:47:37
 */
public interface EventMulticaster {
    /**
     * 增加监听者
     * @param listener
     */
    void addListener(BasicCodeListener<?> listener);

    /**
     * 增加监听者
     * @param listener
     */
    void removeListener(BasicCodeListener<?> listener);

    /**e
     * 广播事件
     * @param event
     */
    void multicastEvent(BasicCodeEvent event);
}
