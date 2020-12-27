package com.fpp.code.org;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Administrator
 */
public class QueueEvent {
    private ConcurrentLinkedQueue queue;

    public Object getSource() {
        return this.queue.poll();
    }

    public QueueEvent(ConcurrentLinkedQueue data) {
        this.queue = data;
    }
}