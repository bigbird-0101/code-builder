package io.github.bigbird0101.code.core.event;

/**
 * 模板事件
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-20 23:03:53
 */
public abstract class TemplateEvent extends BasicCodeEvent{
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public TemplateEvent(Object source) {
        super(source);
    }
}
