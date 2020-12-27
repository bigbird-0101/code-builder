package com.fpp.code.org;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/30 9:09
 */
public class Consumer implements QueueObserver {
    @Override
    public void queueEvent(QueueEvent event) {
        System.out.println("消费了:"+event.getSource());
    }
}
