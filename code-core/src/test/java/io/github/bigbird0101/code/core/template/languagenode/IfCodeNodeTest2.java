package io.github.bigbird0101.code.core.template.languagenode;

import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.template.languagenode.DynamicCodeNodeContext;
import io.github.bigbird0101.code.core.template.languagenode.IfCodeNode;
import io.github.bigbird0101.code.core.template.languagenode.MixCodeNode;
import io.github.bigbird0101.code.core.template.languagenode.StaticTextCodeNode;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-30 23:00:45
 */
class IfCodeNodeTest2 {

    @Test
    void apply() {
        StandardEnvironment standardEnvironment=new StandardEnvironment();
        Map<String,Object> map=new HashMap<>();
        map.put("test","ab");
        map.put("test2","ab");
        DynamicCodeNodeContext dynamicCodeNodeContext=new DynamicCodeNodeContext(map,standardEnvironment);
        final StaticTextCodeNode staticTextNode1 = new StaticTextCodeNode("ab;\n");
        IfCodeNode ifCodeNode2=new IfCodeNode("test2=='ab'",new StaticTextCodeNode("          test2ab\n"));
        IfCodeNode ifCodeNode=new IfCodeNode("test=='ab'",new MixCodeNode(Arrays.asList(staticTextNode1,ifCodeNode2)));
        StaticTextCodeNode staticTextCodeNode=new StaticTextCodeNode("private String abcd;\n");
        StaticTextCodeNode staticTextCodeNode2=new StaticTextCodeNode("private String abcd;\n");
        MixCodeNode mixCodeNode=new MixCodeNode(Arrays.asList(staticTextCodeNode,ifCodeNode,staticTextCodeNode2));
        mixCodeNode.apply(dynamicCodeNodeContext);
        final String code = dynamicCodeNodeContext.getCode();
        System.out.println(code);
    }
}