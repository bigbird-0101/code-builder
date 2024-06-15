package io.github.bigbird0101.code.core.share;

import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author bigbird-0101
 * @date 2024-06-08 22:54
 */
@Disabled
class ShareServerTest {
    @Test
     public void test() throws InterruptedException {
//         StandardEnvironment environment=new StandardEnvironment();
//         GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
//         ShareServer shareServer = new ShareServer();
//         shareServer.setTemplateContext(genericTemplateContext);
//         shareServer.init();
//         shareServer.start();
//         new CountDownLatch(1).await();
        HttpUtil.createServer(123).start();
     }
}