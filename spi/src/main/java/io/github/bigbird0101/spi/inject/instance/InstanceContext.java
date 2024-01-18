package io.github.bigbird0101.spi.inject.instance;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InstanceContext {
    private static volatile InstanceContext instanceContext;
    private InstanceContext() {
    }

    public static InstanceContext getInstance(){
        if(null==instanceContext){
             synchronized (InstanceContext.class){
                 if(null==instanceContext){
                     instanceContext=new InstanceContext();
                 }
             }
        }
        return instanceContext;
    }

    private final Map<String,Object> pool=new ConcurrentHashMap<>();

    public void register(String name,Object object){
        Object exists = pool.get(name);
        if(null==exists) {
            synchronized (pool) {
                pool.putIfAbsent(name, object);
            }
        }else{
            throw new IllegalStateException(String.format("name :%s object already registered",name));
        }
    }

    public Optional<Object> get(String name){
        return Optional.ofNullable(pool.get(name));
    }

    public <T> Optional<Object> get(String name,Class<T> type){
        return Optional.ofNullable(pool.get(name)).filter(s->s.getClass().equals(type));
    }

    public void init(){

    }

    public void destroy(){
        pool.clear();
    }
}
