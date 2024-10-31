package io.github.bigbird0101.code.fxui.event;

import io.github.bigbird0101.code.core.event.TemplateEvent;

public class DatasourceConfigUpdateEvent extends TemplateEvent {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DatasourceConfigUpdateEvent(Object source) {
        super(source);
    }
}
