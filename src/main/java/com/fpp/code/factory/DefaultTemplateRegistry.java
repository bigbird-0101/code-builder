package com.fpp.code.factory;

import com.fpp.code.factory.config.TemplateRegisty;
import com.fpp.code.template.Template;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultTemplateRegistry implements TemplateRegisty {
    private final Map<String, Template> templateMap=new HashMap<>();
    private final Set<String> templateNameSets=new HashSet<>();
    @Override
    public void registerTemplate(String templateName, Template template) {
        Template templateTemp=templateMap.get(templateName);
        if(templateTemp!=null){
            throw new IllegalStateException("Could not register Template [" + template +
                    "] under temolate name '" + templateName + "': there is already object [" + templateTemp + "] bound");
        }
        templateMap.put(templateName,template);
        templateNameSets.add(templateName);
    }

    @Override
    public Template getTemplate(String templateName) {
        return templateMap.get(templateName);
    }

    public void removeTemplate(String templateName){
        templateMap.remove(templateName);
        templateNameSets.remove(templateName);
    }
}
