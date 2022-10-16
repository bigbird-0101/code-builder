package io.github.bigbird0101.code.core.factory;

import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.template.Template;

import java.io.File;

import static cn.hutool.core.io.resource.ResourceUtil.getResourceObj;
import static cn.hutool.core.text.CharSequenceUtil.removePrefix;

/**
 * 模板定义构建器
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-27 22:38:04
 */
public class TemplateDefinitionBuilder {
    public static final String SEPARATOR = File.separator;
    public static final String DEFAULT_SRC_PACKAGE = StrUtil.format("com{}code{}builder{}example",SEPARATOR,SEPARATOR,SEPARATOR);

    public static final String DEFAULT_SOURCES_ROOT = StrUtil.format("src{}main{}java", SEPARATOR,SEPARATOR,SEPARATOR);
    public static final String DEFAULT_MODULE = SEPARATOR;
    public static final String DEFAULT_PROJECT_NAME = "code-builder-example";
    public static final String DEFAULT_FILE_SUFFIX_NAME = "java";

    public static <T extends Template> TemplateDefinition build(Class<T> tClass){
        GenericTemplateDefinition templateDefinition=new GenericTemplateDefinition();
        templateDefinition.setTemplateClassName(tClass.getName());
        templateDefinition.setProjectUrl(SystemUtil.getUserInfo().getHomeDir()+ File.separator + DEFAULT_PROJECT_NAME);
        templateDefinition.setModule(DEFAULT_MODULE);
        templateDefinition.setSourcesRoot(DEFAULT_SOURCES_ROOT);
        templateDefinition.setSrcPackage(DEFAULT_SRC_PACKAGE);
        templateDefinition.setTargetFileSuffixName(DEFAULT_FILE_SUFFIX_NAME);
        return templateDefinition;
    }

    public static <T extends Template> TemplateDefinition build(Class<T> tClass,String templateFilePath){
        final GenericTemplateDefinition templateDefinition = (GenericTemplateDefinition) build(tClass);
        final String filePath = removePrefix(getResourceObj(templateFilePath).getUrl().getFile(), SEPARATOR);
        templateDefinition.setTemplateFile(new File(filePath));
        return templateDefinition;
    }
}
