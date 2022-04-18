package com.fpp.code.core.template;

import com.alibaba.fastjson.annotation.JSONType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;

/**
 * 有依赖模板的项目模板
 * @author Administrator
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class HaveDependTemplateHandleFunctionTemplate extends DefaultHandleFunctionTemplate implements HaveDependTemplate{
    private static Logger logger= LogManager.getLogger(HaveDependTemplateHandleFunctionTemplate.class);

    private LinkedHashSet<String> dependTemplates;

    @Override
    public LinkedHashSet<String> getDependTemplates() {
        return dependTemplates;
    }

    public void setDependTemplates(LinkedHashSet<String> dependTemplates) {
        this.dependTemplates = dependTemplates;
    }

    public HaveDependTemplateHandleFunctionTemplate(){
    }
}
