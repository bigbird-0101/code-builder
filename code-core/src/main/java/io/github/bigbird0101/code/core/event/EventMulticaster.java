package io.github.bigbird0101.code.core.event;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-20 22:47:37
 */
public interface EventMulticaster {
    /**
     * 增加监听者
     * @param listener 监听者
     */
    void addListener(BasicCodeListener<?> listener);

    /**
     * 增加监听者
     * @param listener 监听者
     */
    void removeListener(BasicCodeListener<?> listener);

    /**e
     * 广播事件
     * @param event 事件
     */
    void multicastEvent(BasicCodeEvent event);
}
