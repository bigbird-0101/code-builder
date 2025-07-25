package io.github.bigbird0101.code.core.template.resolver;

import cn.hutool.core.collection.CollectionUtil;
import io.github.bigbird0101.code.core.context.TemplateContext;
import io.github.bigbird0101.code.core.context.aware.TemplateContextAware;
import io.github.bigbird0101.code.core.domain.TableInfo;
import io.github.bigbird0101.code.core.template.AbstractTemplateLangResolver;
import io.github.bigbird0101.code.core.template.AbstractAbstractTemplateResolver;
import io.github.bigbird0101.code.core.template.HaveDependTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.core.template.TemplateResolver;
import io.github.bigbird0101.code.core.template.TemplateTraceContext;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import io.github.bigbird0101.code.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fpp
 * @version 1.0
 */
public class DependTemplateLangResolver extends AbstractTemplateLangResolver implements TemplateContextAware {
    private static final Logger LOGGER = LogManager.getLogger(DependTemplateLangResolver.class);

    private TemplateContext templateContext;

    public DependTemplateLangResolver() {
        super();
        this.resolverName=LANG_NAME;
    }

    @Override
    public void setTemplateContext(TemplateContext templateContext) {
        this.templateContext=templateContext;
    }

    /**
     * 工具类方法枚举
     */
    private enum Function{
        /**
         * 所依赖的模板全类名
         */
        SIMPLE_CLASS_NAME("simpleClassName"){
            @Override
            String done(String index,Map<String,Object> dataModel) throws TemplateResolveException {
                Template currentTemplate = TemplateTraceContext.getCurrentTemplate();
                if(currentTemplate instanceof HaveDependTemplate){
                    final HaveDependTemplate currentTemplateWithDepend = (HaveDependTemplate) currentTemplate;
                    Template templateDepend = getDependTemplate(index, currentTemplateWithDepend);
                    if(null==templateDepend){
                        final String templateDependName = new ArrayList<>(currentTemplateWithDepend.getDependTemplates())
                                .get(Integer.parseInt(index));
                        throw new IllegalArgumentException("depend "+templateDependName+" template is not exists ");
                    }
                    TableInfo tableInfo = (TableInfo) dataModel.get("tableInfo");
                    final String tableName = Optional.ofNullable(tableInfo).map(TableInfo::getTableName).orElse(null);
                    String prefixStrategy = templateDepend.getTargetFilePrefixNameStrategy().prefixStrategy(templateDepend, tableName, dataModel);
                    TemplateTraceContext.setCurrentTemplate(currentTemplate);
                    return prefixStrategy;
                }else{
                    throw new TemplateResolveException(String.format("current template %s is not HaveDependTemplate,but it use  %s[%s].%s",currentTemplate.getTemplateName(),LANG_NAME,index,"className"));
                }
            }
        },
        /**
         * 所依赖的模板全类名
         */
        CLASS_NAME("className"){
            @Override
            String done(String index,Map<String,Object> dataModel) throws TemplateResolveException {
                Template currentTemplate = TemplateTraceContext.getCurrentTemplate();
                if(currentTemplate instanceof HaveDependTemplate){
                    Template templateDepend = getDependTemplate(index, (HaveDependTemplate) currentTemplate);
                    TableInfo tableInfo = (TableInfo) dataModel.get("tableInfo");
                    final String tableName = Optional.ofNullable(tableInfo).map(TableInfo::getTableName).orElse(null);
                    String className = Utils.pathToPackage(templateDepend.getSrcPackage()) + "." +
                            templateDepend.getTargetFilePrefixNameStrategy().prefixStrategy(templateDepend, tableName, dataModel);
                    TemplateTraceContext.setCurrentTemplate(currentTemplate);
                    return className;
                }else{
                    throw new TemplateResolveException(String.format("current template %s is not HaveDependTemplate,but it use  %s[%s].%s",currentTemplate.getTemplateName(),LANG_NAME,index,"className"));
                }
            }
        },
        PACKAGE_NAME("packageName"){
            @Override
            String done(String index,Map<String,Object> dataModel) throws TemplateResolveException {
                Template currentTemplate = TemplateTraceContext.getCurrentTemplate();
                if(currentTemplate instanceof HaveDependTemplate){
                    Template templateDepend = getDependTemplate(index, (HaveDependTemplate) currentTemplate);
                    String packageName = Utils.pathToPackage(templateDepend.getSrcPackage());
                    TemplateTraceContext.setCurrentTemplate(currentTemplate);
                    return packageName;
                }else{
                    throw new TemplateResolveException(String.format("current template %s is not HaveDependTemplate but it  %s[%s].%s",currentTemplate.getTemplateName(),LANG_NAME,index,"srcPackage"));
                }
            }
        }
        ;

        private TemplateContext templateContext;
        private final String value;
        public String getValue() {
            return value;
        }
        public TemplateContext getTemplateContext() {
            return templateContext;
        }
        public void setTemplateContext(TemplateContext templateContext) {
            this.templateContext = templateContext;
        }
        Function(String value) {
            this.value = value;
        }
        abstract String done(String index,Map<String,Object> dataModel) throws TemplateResolveException;


        Template getDependTemplate(String index, HaveDependTemplate currentTemplate) throws TemplateResolveException {
            int i;
            try {
                i = Integer.parseInt(index);
            }catch (Exception e){
                throw new TemplateResolveException(String.format("get index %s depend template error to get template",index));
            }
            if(CollectionUtil.isEmpty(currentTemplate.getDependTemplates())){
                throw new TemplateResolveException(String.format("have depend template [ %s ] not to get depend template config",currentTemplate.getTemplateName()));
            }
            Template templateDepend;
            String templateName = getSetValue(currentTemplate.getDependTemplates(), i);
            templateDepend = getTemplateContext().getTemplate(templateName);
            return templateDepend;
        }
    }

    private static final String LANG_NAME="depend";
    public static final Pattern DEPEND_TEMPLATE_PATTERN = Pattern.compile("(\\s*" + AbstractAbstractTemplateResolver.TEMPLATE_VARIABLE_PREFIX_ESCAPE + "\\s*" + LANG_NAME + "\\s*\\[\\s*(?<index>.*?)\\s*\\]\\s*\\.(?<function>.*?)\\(?\\s*\\s*\\)??\\s*" + AbstractAbstractTemplateResolver.TEMPLATE_VARIABLE_SUFFIX_ESCAPE + "\\s*)", Pattern.DOTALL);
    protected static final Pattern TEMPLATE_GRAMMAR_PATTERN_SUFFIX = Pattern.compile("(\\s*"+LANG_NAME+"\\s*\\[\\s*.*?\\s*\\]\\s*\\.(?<function>.*?)\\(?\\s*\\)?\\s*)", Pattern.DOTALL);
    private final Set<Pattern> excludeVariablePatten=new HashSet<>(Collections.singletonList(TEMPLATE_GRAMMAR_PATTERN_SUFFIX));

    public DependTemplateLangResolver(TemplateResolver templateResolver) {
        super(templateResolver);
        this.resolverName=LANG_NAME;
    }

    /**
     * 获取 模板排除某些正则key 这些正则key是模板中语言的 类型 的set
     *
     * @return
     */
    @Override
    public Set<Pattern> getExcludeVariablePatten() {
        return excludeVariablePatten;
    }

    @Override
    public boolean matchLangResolver(String srcData) {
        return DEPEND_TEMPLATE_PATTERN.matcher(srcData).find();
    }

    /**
     * 模板语言解析方法
     *
     * @param srcData         需要解析的模板数据
     * @param dataModal 模板中的变量数据
     */
    @Override
    public String langResolver(String srcData, Map<String, Object> dataModal) throws TemplateResolveException {
        Matcher matcher= DEPEND_TEMPLATE_PATTERN.matcher(srcData);
        String result="";
        while(matcher.find()){
            String function = matcher.group("function");
            String index = matcher.group("index");
            String all=matcher.group(0);
            Function functionTemp=checkFunction(function);
            functionTemp.setTemplateContext(templateContext);
            String mendLastStr = Utils.getLastNewLineNull(all);
            String mendFirstStr = Utils.getFirstNewLineNull(all);
            String bodyResult=functionTemp.done(index, dataModal);
            bodyResult= mendFirstStr +bodyResult+ mendLastStr;
            result = Utils.isEmpty(result) ? srcData.replace(all, bodyResult) : result.replace(all, bodyResult);
        }
        return Utils.isEmpty(result)?srcData:result;
    }

    private Function checkFunction(String functionName) {
        //校验工具方法是否存在
        Function result=null;
        for(Function function: Function.values()){
            if(function.getValue().equals(functionName)){
                result=function;
            }
        }
        Objects.requireNonNull(result,functionName+"在"+LANG_NAME+"中不存在");
        return result;
    }

    private static String getSetValue(Set<String> set, int index) {
        int result = 0;
        for (String entry:set) {
            if(result==index){
                return entry;
            }
            result++;
        }
        return "";
    }
}
