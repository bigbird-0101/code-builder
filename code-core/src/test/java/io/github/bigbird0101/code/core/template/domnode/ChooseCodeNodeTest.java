package io.github.bigbird0101.code.core.template.domnode;

import io.github.bigbird0101.code.core.DataModelProvider;
import io.github.bigbird0101.code.core.ExceptResultProvider;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.template.Template;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChooseCodeNodeTest {
    @Test
    void testChooseCodeNode() {
        GenericTemplateContext genericTemplateContext = new GenericTemplateContext();
        Template template = genericTemplateContext.getTemplate("testCodeChooseNodeTableInfoXml");
        Map<String, Object> dataModel = DataModelProvider.getDataModel();
        dataModel.put("respVoSimpleName", "UserRespVo");
        dataModel.put("doSimpleName", "User");
        dataModel.put("firstLowerDoSimpleName", "UserService");
        String process = template.process(dataModel);
        assertEquals(ExceptResultProvider.getExceptResult("testCodeChooseNodeTableInfoXml"), process);
    }
}
