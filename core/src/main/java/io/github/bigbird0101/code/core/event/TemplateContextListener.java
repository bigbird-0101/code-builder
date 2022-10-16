package io.github.bigbird0101.code.core.event;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-21 22:26:11
 */
public abstract class TemplateContextListener<T extends TemplateContextEvent> implements BasicCodeListener<T> {
    @Override
    public void onBasicCodeEvent(T t) {
        onTemplateContextEvent(t);
    }

    /**
     * 触发模板事件处理
     * @param t 模板事件
     */
    protected abstract void onTemplateContextEvent(T t);
}
