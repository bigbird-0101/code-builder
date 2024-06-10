package io.github.bigbird0101.code.core.share;

import org.junit.jupiter.api.Test;

/**
 * @author bigbird-0101
 * @date 2024-06-10 22:07
 */
class ShareClientTest {
    ShareClient shareClient=new ShareClient();
    @Test
    void multipleTemplate() {
    }

    @Test
    void template() {
    }

    @Test
    void templateContent() {
        String templateContent = shareClient.templateContent("http://localhost:4321/template?t=Dao");
        System.out.println(templateContent);
    }
}