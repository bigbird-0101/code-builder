package main.java.template;

import main.java.common.Utils;
import main.java.config.CodeConfigException;
import main.java.config.ProjectFileConfig;

import java.io.IOException;
import java.util.Map;

/**
 * 不需要操作方法的模板
 * @author fpp
 * @version 1.0
 * @date 2020/6/9 17:45
 */
public abstract class NoHandleFunctionTemplate extends AbstractTemplate {
    private String templateContent;
    public NoHandleFunctionTemplate(String templeFileName, ProjectFileConfig projectFileConfig) throws IOException, CodeConfigException {
        super(templeFileName,projectFileConfig);
        refresh(templeFileName);
    }

    public NoHandleFunctionTemplate(String templeFileName) throws IOException, CodeConfigException {
        super(templeFileName);
        refresh(templeFileName);
    }

    public void refresh(String templeFileName) throws IOException {
        if(Utils.isNotEmpty(templeFileName)) {
            this.templateContent = readTempleteFile(templeFileName);
        }
    }


    @Override
    public String getTempleResult(Map<String, Object> replaceKeyValue) throws TemplateResolveException {
        return getTemplateResolver().resolver(this.templateContent,replaceKeyValue);
    }

}
