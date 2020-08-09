package main.java.template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import main.java.common.Utils;
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

    private JSONObject jsonObject;

    public DefaultMultipleTemplate() {
        this(null);
    }

    public DefaultMultipleTemplate(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public List<Template> getDefaultTemplates() throws IOException, CodeConfigException {
        List<Template> templateList = new ArrayList<>(5);
        if (null == this.jsonObject || this.jsonObject.isEmpty()) {
            Template templateDomain = new DefaultNoHandleFunctionTemplate("DoMain模板", "DoMainTemplate.txt", "domain");
            templateDomain.setTemplateFileNameStrategy(new OnlySubFourTemplateFilePrefixNameStrategy());
            templateList.add(templateDomain);
            templateList.add(new DefaultHandleFunctionTemplate("Controller模板", "ControllerFileTemplate.txt", "controller"));
            templateList.add(new DefaultHandleFunctionTemplate("Dao模板", "DaoFileTemplate.txt", "dao"));
            Template serviceTemplate = new DefaultHandleFunctionTemplate("ServiceInterface模板", "ServiceInterfaceFileTemplate.txt", "service");
            templateList.add(new DefaultHandleFunctionTemplate("ServiceImpl模板", "ServiceImplFileTemplate.txt", serviceTemplate, "service/impl"));
            templateList.add(serviceTemplate);
        } else {
            Iterator<Map.Entry<String, Object>> iterator=jsonObject.entrySet().iterator();
            TEMPLATE_SPRING=jsonObject.keySet().stream().findFirst().get();
            while(iterator.hasNext()){
                JSONArray templateArray= (JSONArray) iterator.next().getValue();
                for(Object obj:templateArray) {
                    JSONObject template=(JSONObject)obj;
                    String url = template.getString("url");
                    String name = template.getString("name");
                    String path = template.getString("path");
                    int fileNameStrategyType = template.getIntValue("fileNameStrategy");
                    int isHandleFunction = template.getIntValue("isHandleFunction");
                    Template parent = Utils.isEmpty(template.getString("parent")) ? null : getParentTemplate(templateList, template.getString("parent"));
                    Template resultTemplate;
                    if (isHandleFunction == 1) {
                        resultTemplate = new DefaultNoHandleFunctionTemplate(name, url, parent, path);
                    } else {
                        resultTemplate = new DefaultHandleFunctionTemplate(name, url, parent, path);
                    }
                    if (fileNameStrategyType == 0) {
                        resultTemplate.setTemplateFileNameStrategy(new DefaultTemplateFilePrefixNameStrategy());
                    } else if (fileNameStrategyType == 1) {
                        resultTemplate.setTemplateFileNameStrategy(new OnlySubFourTemplateFilePrefixNameStrategy());
                    }
                    templateList.add(resultTemplate);
                }
            }
        }
        return templateList;
    }

    /**
     * 根据父模板名获取父模板对象
     * @param templateList
     * @param templateName
     * @return
     */
    private Template getParentTemplate(List<Template> templateList,String templateName){
        return templateList.stream().filter(template -> template.getTemplateName().equals(templateName)).findFirst().get();
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
        List<Template> templateListTemp = getDefaultTemplates();
        this.templateList = templateListTemp;
        return templateListTemp;
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
