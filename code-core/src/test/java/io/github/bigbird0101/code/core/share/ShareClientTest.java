package io.github.bigbird0101.code.core.share;

import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author bigbird-0101
 * @date 2024-06-10 22:07
 */
class ShareClientTest {
    ShareClient shareClient=new ShareClient();


    @BeforeEach
    void before() {
        StandardEnvironment environment = new StandardEnvironment();
        GenericTemplateContext genericTemplateContext = new GenericTemplateContext(environment);
        shareClient.setTemplateContext(genericTemplateContext);
    }

    @Test
    void multipleTemplate() {
    }

    @Test
    void template() {
        TemplateDefinitionWrapper template = shareClient.template("http://localhost:4321/template?t=Dao");
        System.out.println(template);
    }

    @Test
    void templateContent() {
        String templateContent = shareClient.templateContent("http://localhost:4321/template?t=Dao");
        System.out.println(templateContent);
    }
}