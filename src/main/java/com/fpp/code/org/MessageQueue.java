package com.fpp.code.org;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/30 9:35
 */
public class MessageQueue {
    //按每3个一组分割
    private static final Integer MAX_NUMBER = 3;

//    private Executor executor= Executors.newFixedThreadPool(20,r -> {
//        Thread thread=new Thread(r);
//        thread.setDaemon(false);
//        return thread;
//    });
    public MessageQueue() {
    }
    /**
     * 计算切分次数
     */
    private static Integer countStep(Integer size) {
        return (size + MAX_NUMBER - 1) / MAX_NUMBER;
    }

    private final ConcurrentLinkedQueue concurrentLinkedQueue=new ConcurrentLinkedQueue();

    private List<QueueObserver> addQueueObserverList=new ArrayList<>(10);

    public void add(Object o){
        concurrentLinkedQueue.add(o);

        addQueueObserverList.forEach(item->{
            if(!concurrentLinkedQueue.isEmpty()) {
                item.queueEvent(new QueueEvent(concurrentLinkedQueue));
            }
        });
//        int limit=countStep(addQueueObserverList.size());
//        List<List<QueueObserver>> splitList = Stream.iterate(0, n -> n + 1).limit(limit).map(a -> addQueueObserverList.stream().skip(a * MAX_NUMBER).limit(MAX_NUMBER).collect(Collectors.toList())).collect(Collectors.toList());
//        splitList.stream().map(list->CompletableFuture.supplyAsync(()->{
//                list.forEach(item -> {
//                    if (!concurrentLinkedQueue.isEmpty()) {
//                        item.queueEvent(new QueueEvent(concurrentLinkedQueue));
//                    }
//                });
//                return null;
//        },executor)).map(s->s.join()).collect(Collectors.toList());
    }

    public void subscribe(QueueObserver addQueueObserver){
        addQueueObserverList.add(addQueueObserver);
    }

    public void subscribe(List<QueueObserver> queueObserverList){
        addQueueObserverList.addAll(queueObserverList);
    }

    public static void main(String[] args) {

    }

}
