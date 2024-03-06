package io.github.bigbird0101.code.core.event;

import cn.hutool.log.StaticLog;
import io.github.bigbird0101.spi.SPIServiceLoader;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-21 22:33:49
 */
public abstract class AbstractEventMulticaster implements EventMulticaster {

    private final Object lock=new Object();
    private final Set<BasicCodeListener<?>> listenerSet=new HashSet<>();

    {
        SPIServiceLoader.loadServiceInstances(BasicCodeListener.class)
                .forEach(this::addListener);
    }

    @Override
    public void addListener(BasicCodeListener<?> listener) {
        synchronized (lock){
            listenerSet.add(listener);
        }
    }

    @Override
    public void removeListener(BasicCodeListener<?> listener) {
        synchronized (lock){
            listenerSet.remove(listener);
        }
    }

    protected List<BasicCodeListener<?>> getListeners(BasicCodeEvent event) {
        List<BasicCodeListener<?>> allListeners = new ArrayList<>();
        Set<BasicCodeListener<?>> listeners;
        synchronized (this.lock) {
            listeners = new LinkedHashSet<>(this.listenerSet);
        }
        for (BasicCodeListener<?> listener:listeners){
            final Type genericSuperclass = listener.getClass().getGenericSuperclass();
            if(getType(genericSuperclass).equals(event.getClass().getName())){
                StaticLog.info("get listener {}",listener);
                allListeners.add(listener);
            }
        }
        return allListeners;
    }

    public String getType(Type type){
        //获取到类型但是是Type类型，不是实际参数类型（是Type的子接口），要强转为实参型
        ParameterizedType param = (ParameterizedType) type;
        //父类泛型可能有好几个，所以是个数组，本梨中需要知道的是DTO，也就是第一个泛型
        Type actualTypeArgument = param.getActualTypeArguments()[0];
        return actualTypeArgument.getTypeName();
    }
}
