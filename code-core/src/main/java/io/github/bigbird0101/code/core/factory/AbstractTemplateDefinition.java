package io.github.bigbird0101.code.core.factory;

import com.alibaba.fastjson.annotation.JSONField;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;

import java.io.File;
import java.util.Objects;

/**
 * @author fpp
 */
public abstract class AbstractTemplateDefinition implements TemplateDefinition {

    private String projectUrl;

    private String module;

    private String sourcesRoot;

    private String srcPackage;

    private File templateFile;

    @JSONField(alternateNames = {"templateClassName"})
    private Object templateClass;


    @Override
    public void setTemplateClassName(String templateClassName) {
        this.templateClass=templateClassName;
    }

    @Override
    public String getTemplateClassName() {
        Object templateClassObject = this.templateClass;
        if (templateClassObject instanceof Class) {
            return ((Class<?>) templateClassObject).getName();
        }
        else {
            return (String) templateClassObject;
        }
    }

    public void setTemplateClass(Class<?> templateClass){
        this.templateClass=templateClass;
    }

    public Class<?> getTemplateClass() {
        Object beanClassObject = this.templateClass;
        if (beanClassObject == null) {
            throw new IllegalStateException("No bean class specified on bean definition");
        }
        if (!(beanClassObject instanceof Class)) {
            throw new IllegalStateException(
                    "Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
        }
        return (Class<?>) beanClassObject;
    }

    @Override
    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    @Override
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public String getSourcesRoot() {
        return sourcesRoot;
    }

    public void setSourcesRoot(String sourcesRoot) {
        this.sourcesRoot = sourcesRoot;
    }

    @Override
    public String getSrcPackage() {
        return srcPackage;
    }

    public void setSrcPackage(String srcPackage) {
        this.srcPackage = srcPackage;
    }

    @Override
    public File getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(File templateFile) {
        this.templateFile = templateFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTemplateDefinition that = (AbstractTemplateDefinition) o;
        return Objects.equals(projectUrl, that.projectUrl) &&
                Objects.equals(module, that.module) &&
                Objects.equals(sourcesRoot, that.sourcesRoot) &&
                Objects.equals(srcPackage, that.srcPackage) &&
                Objects.equals(templateFile, that.templateFile) &&
                Objects.equals(templateClass, that.templateClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectUrl, module, sourcesRoot, srcPackage, templateFile, templateClass);
    }

}
