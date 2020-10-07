package main.java.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import main.java.config.CodeConfigException;
import main.java.config.ProjectFileConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 默认的组合模板
 *
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 18:17
 */
public class DefaultMultipleTemplate extends AbstractMultipleTemplate {

    private List<Template> templateList;
    private static String TEMPLATE_SPRING = "Spring项目模板";

    private TemplateFactory templateFactory;
    private JSONObject fileConfig;

    public DefaultMultipleTemplate() {
        this(null);
    }

    public DefaultMultipleTemplate(JSONObject jsonObject) {
        this.fileConfig = jsonObject;
        this.templateFactory=new GenericTemplateFactory();
    }

    public List<Template> getDefaultTemplates() throws IOException, CodeConfigException {
        templateList = new ArrayList<>(5);
        GenericTemplateFactory templateFactoryReal=(GenericTemplateFactory)templateFactory;
        if (null == this.fileConfig || this.fileConfig.isEmpty()) {
            templateFactoryReal.setTemplateConfigList(getDefaultTemplateConfigList());
            templateList.addAll(templateFactoryReal.getTemplates());
        } else {
            Iterator<Map.Entry<String, Object>> iterator= fileConfig.entrySet().iterator();
            TEMPLATE_SPRING= fileConfig.keySet().stream().findFirst().get();
            while(iterator.hasNext()){
                JSONArray templateArray= (JSONArray) iterator.next().getValue();
                List<TemplateConfigDomain> templateConfigList=new ArrayList<>(templateArray.size());
                for(Object obj:templateArray) {
                    JSON template=((JSON)obj);
                    TemplateConfigDomain templateConfigDomain=template.toJavaObject(TemplateConfigDomain.class);
                    JSONObject templateJSON=(JSONObject)template;
                    templateConfigDomain.setIsHandleFunction(templateJSON.containsKey("isHandleFunction")?templateJSON.getIntValue("isHandleFunction"):1);
                    templateConfigList.add(templateConfigDomain);
                }
                templateFactoryReal.setTemplateConfigList(templateConfigList);
                templateList.addAll(templateFactoryReal.getTemplates());
            }
        }
        return templateList;
    }

    /**
     * 获取默认的模板配置
     * @return
     */
    public List<TemplateConfigDomain> getDefaultTemplateConfigList(){
        List<TemplateConfigDomain> templateConfigList=new ArrayList<>(5);
        templateConfigList.add(new TemplateConfigDomain("META-INF/DoMainTemplate.txt","DoMain模板","domain",1,0,null));
        templateConfigList.add(new TemplateConfigDomain("META-INF/ControllerFileTemplate.txt","Controller模板",  "controller",0,1,null));
        templateConfigList.add(new TemplateConfigDomain("META-INF/DaoFileTemplate.txt","Dao模板",  "dao",0,1,null));
        templateConfigList.add(new TemplateConfigDomain("META-INF/ServiceInterfaceFileTemplate.txt","ServiceInterface模板",  "service",0,1,null));
        templateConfigList.add(new TemplateConfigDomain("META-INF/ServiceImplFileTemplate.txt", "ServiceImpl模板", "service/impl",0,1, "ServiceInterface模板"));
        return templateConfigList;
    }


    /**
     * 获取模板名称
     *
     * @return
     */
    @Override
    public String getTemplateName() {
        return TEMPLATE_SPRING;
    }

    /**
     * 获取多个模板集合
     *
     * @return
     */
    @Override
    public List<Template> getMultipleTemplate(ProjectFileConfig projectFileConfig) throws IOException, CodeConfigException {
        return getDefaultTemplates();
    }

    @Override
    public void setResolverStrategys(Map<String, ResolverStrategy> templateResolverStrategy) {
        this.templateList.forEach(item -> {
            if (item instanceof HandleFunctionTemplate && templateResolverStrategy.containsKey(item.getTemplateName())) {
                ((HandleFunctionTemplate) item).setResolverStrategy(templateResolverStrategy.get(item.getTemplateName()));
            }
        });
    }
}
