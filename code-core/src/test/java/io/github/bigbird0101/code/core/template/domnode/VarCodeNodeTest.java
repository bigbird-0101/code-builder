package io.github.bigbird0101.code.core.template.domnode;

import io.github.bigbird0101.code.core.config.StandardEnvironment;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VarCodeNodeTest {
    @Test
    void apply() {
        StandardEnvironment standardEnvironment=new StandardEnvironment();
        Map<String,Object> map=new HashMap<>();
        map.put("test","ab");
        map.put("test2","ab");
        map.put("testabbb","abbbbbbb");
        DynamicCodeNodeContext dynamicCodeNodeContext=new DynamicCodeNodeContext(map,standardEnvironment);
        final StaticTextCodeNode staticTextNode1 = new StaticTextCodeNode("ab;\n");
        IfCodeNode ifCodeNode2=new IfCodeNode("test2=='ab'",new StaticTextCodeNode("          test2ab\n"));
        IfCodeNode ifCodeNode=new IfCodeNode("test=='ab'",new MixCodeNode(Arrays.asList(staticTextNode1,ifCodeNode2)));
        StaticTextCodeNode staticTextCodeNode=new StaticTextCodeNode("private String abcd;\n");
        StaticTextCodeNode staticTextCodeNode2=new StaticTextCodeNode("private String abcd;*{abcd}*\n");
        VarCodeNode varCodeNode=new VarCodeNode("abcd","*{tool.sub(1,*{testabbb}*)}*");
        MixCodeNode mixCodeNode=new MixCodeNode(Arrays.asList(varCodeNode,staticTextCodeNode,ifCodeNode,staticTextCodeNode2));
        mixCodeNode.apply(dynamicCodeNodeContext);
        final String code = dynamicCodeNodeContext.getCode();
        System.out.println(code);
    }
}
