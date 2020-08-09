package main.java.template;

import main.java.config.CodeConfigException;
import main.java.config.ProjectFileConfig;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 多个模板集合 组合
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 18:19
 */
public interface MultipleTemplate {
    /**
     * 获取模板名称
     * @return
     */
    String getTemplateName();
    /**
     * 获取多个模板集合
     * @param projectFileConfig 模板中需要使用的配置项
     * @return
     */
    List<Template> getMultipleTemplate(ProjectFileConfig projectFileConfig) throws IOException, CodeConfigException;

    /**
     * 设置模板解析策略
     * @param templateResolverStrategy 模板解析策略  k-为模板名 键值为模板解析策略
     */
    default void setResolverStrategys(Map<String,ResolverStrategy> templateResolverStrategy){

    }
}
