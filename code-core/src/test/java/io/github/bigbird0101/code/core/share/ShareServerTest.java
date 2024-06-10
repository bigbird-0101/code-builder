package io.github.bigbird0101.code.core.share;

import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author bigbird-0101
 * @date 2024-06-08 22:54
 */
class ShareServerTest {
    @Test
     public void test() throws InterruptedException {
         StandardEnvironment environment=new StandardEnvironment();
         GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
         ShareServer shareServer = new ShareServer();
         shareServer.setTemplateContext(genericTemplateContext);
         shareServer.init();
         shareServer.start();
         new CountDownLatch(1).await();
     }
}