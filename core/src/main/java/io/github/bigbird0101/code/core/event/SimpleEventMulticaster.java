package io.github.bigbird0101.code.core.event;

import java.util.concurrent.Executor;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-21 23:08:09
 */
public class SimpleEventMulticaster extends AbstractEventMulticaster{
    private Executor executor;

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void multicastEvent(BasicCodeEvent event) {
        Executor executor = getExecutor();
        for (BasicCodeListener<?> listener : getListeners(event)) {
            if (executor != null) {
                executor.execute(() -> invokeListener(listener, event));
            }
            else {
                invokeListener(listener, event);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void invokeListener(BasicCodeListener listener, BasicCodeEvent event) {
        listener.onBasicCodeEvent(event);
    }
}
