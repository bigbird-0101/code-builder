package main.java.org;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/30 9:37
 */
public interface QueueObserver {
    void queueEvent(QueueEvent event);
}
