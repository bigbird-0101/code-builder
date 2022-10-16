package io.github.bigbird0101.code.core.template;

import com.alibaba.fastjson.annotation.JSONType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
/**
 * @author Administrator
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class HaveDependTemplateNoHandleFunctionTemplate extends DefaultNoHandleFunctionTemplate  implements HaveDependTemplate{
    private static Logger logger= LogManager.getLogger(HaveDependTemplateHandleFunctionTemplate.class);

    private Set<String> dependTemplates;

    @Override
    public Set<String> getDependTemplates() {
        return dependTemplates;
    }

    public void setDependTemplates(Set<String> dependTemplates) {
        this.dependTemplates = dependTemplates;
    }
}
