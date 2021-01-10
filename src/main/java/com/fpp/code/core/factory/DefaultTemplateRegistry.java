package com.fpp.code.core.factory;

import com.fpp.code.core.factory.config.TemplateRegisty;
import com.fpp.code.core.template.MultipleTemplate;
import com.fpp.code.core.template.Template;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 默认的模板注册器
 * @author fpp
 */
public class DefaultTemplateRegistry implements TemplateRegisty {
    private final Map<String, Template> templateMap=new HashMap<>();
    protected final Set<String> templateNameSets=new HashSet<>();
    private final Map<String, MultipleTemplate> multipleTemplateMap=new HashMap<>();
    protected final Set<String> multipleTemplateNameSets=new HashSet<>();

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
    public Template getSingletonTemplate(String templateName) {
        return templateMap.get(templateName);
    }

    /**
     * 注册组合模板
     *
     * @param templateName
     * @param template
     */
    @Override
    public void registerMultipleTemplate(String templateName, MultipleTemplate template) {
        MultipleTemplate templateTemp=multipleTemplateMap.get(templateName);
        if(templateTemp!=null){
            throw new IllegalStateException("Could not register MultipleTemplate [" + template +
                    "] under MultipleTemplate name '" + templateName + "': there is already object [" + templateTemp + "] bound");
        }
        multipleTemplateMap.put(templateName,template);
        multipleTemplateNameSets.add(templateName);
    }

    /**
     * 获取组合模板
     *
     * @param templateName
     * @return
     */
    @Override
    public MultipleTemplate getSingletonMultipleTemplate(String templateName) {
        return multipleTemplateMap.get(templateName);
    }

    @Override
    public MultipleTemplate replaceMultipleTemplate(MultipleTemplate multipleTemplate) {
        return multipleTemplateMap.put(multipleTemplate.getTemplateName(),multipleTemplate);
    }

    @Override
    public Template replaceTemplate(Template template) {
        return templateMap.put(template.getTemplateName(),template);
    }

    public void removeTemplate(String templateName){
        templateMap.remove(templateName);
        templateNameSets.remove(templateName);
    }
}
