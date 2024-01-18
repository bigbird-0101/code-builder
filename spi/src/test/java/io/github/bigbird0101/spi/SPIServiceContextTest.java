package io.github.bigbird0101.spi;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SPIServiceContextTest {

    @Test
    void testLoadServiceInstances() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        Set<Collection<TestSPI>> list=new HashSet<>();
        List<Future<Collection<TestSPI>>> listFuture=new ArrayList<>();
        for(int a=0;a<1000;a++) {
            Future<Collection<TestSPI>> submit = executorService.submit(() -> SPIServiceContext.getInstance().loadServiceInstances(TestSPI.class));
            listFuture.add(submit);
        }
        for (Future<Collection<TestSPI>> collectionFuture:listFuture){
            list.add(collectionFuture.get());
        }
        Set<TestSPI> collect = list.stream().flatMap(Collection::stream).collect(Collectors.toSet());
        assertEquals(3,collect.size());
        executorService.shutdown();
    }
}
