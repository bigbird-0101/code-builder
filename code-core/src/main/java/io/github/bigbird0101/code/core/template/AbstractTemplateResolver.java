package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.context.aware.TemplateContextAware;
import io.github.bigbird0101.code.core.context.aware.TemplateContextProvider;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.template.resolver.ToolTemplateLangResolver;
import io.github.bigbird0101.code.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.text.StrPool.DOT;

/**
 * @author fpp
 * @version 1.0
 * @since 2020/6/15 13:29
 */
public abstract class AbstractTemplateResolver  extends TemplateContextProvider implements TemplateResolver, Serializable {

    private static final Logger LOGGER = LogManager.getLogger(AbstractTemplateResolver.class);

    protected static final String CONFIG_TEMPLATE_RESOLVERS="code.project.file.template-lang-resolver";

    /**
     * 模板变量的前缀标识
     * 模板变量使用 {{变量名}}
     */
    public static final String TEMPLATE_VARIABLE_PREFIX = "\\*\\{";

    /**
     * 模板变量的后缀标识
     * 模板变量使用 {{变量名}}
     */
    public static final String TEMPLATE_VARIABLE_SUFFIX = "\\}\\*";

    /**
     * 变量key pattern group
     */
    public static final String TEMPLATE_VARIABLE_KEY = "variableKey";

    /**
     * 获取模板变量{{}}的key值
     */
    public static final Pattern TEMPLATE_VARIABLE_KEY_PATTERN = Pattern.compile("(?<=" + TEMPLATE_VARIABLE_PREFIX + ")(?<"+TEMPLATE_VARIABLE_KEY+">.*?)(?=" + TEMPLATE_VARIABLE_SUFFIX + ")");


    private List<TemplateLangResolver> templateLangResolverList;

    private Environment environment;

    public List<TemplateLangResolver> getTemplateLangResolverList() {
        return templateLangResolverList;
    }

    public void setTemplateLangResolverList(List<TemplateLangResolver> templateLangResolverList) {
        this.templateLangResolverList = templateLangResolverList;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public AbstractTemplateResolver() throws CodeConfigException {
        init();
        initTemplateLangResolverListWhenConfig(null);
    }

    public AbstractTemplateResolver(Environment environment) throws CodeConfigException {
        init();
        this.environment=environment;
        initTemplateLangResolverListWhenConfig(environment);
    }

    public void init(){
        this.templateLangResolverList=new CopyOnWriteArrayList<>();
        ServiceLoader<TemplateLangResolver> serviceLoader=ServiceLoader.load(TemplateLangResolver.class);
        serviceLoader.forEach(item->{
            item.setTemplateResolver(this);
            if(item instanceof TemplateContextAware){
                TemplateContextAware temp=(TemplateContextAware)item;
                temp.setTemplateContext(getTemplateContext());
            }
            this.templateLangResolverList.add(item);
        });
        if(templateLangResolverList.size()==0&& LOGGER.isWarnEnabled()){
            LOGGER.warn("template lang resolver load failed");
        }
    }

    private void  initTemplateLangResolverListWhenConfig(Environment environment) throws CodeConfigException {
        if(null!=environment) {
            String templateLangResolverListClassNameStr = environment.getProperty(CONFIG_TEMPLATE_RESOLVERS);
            if (Utils.isNotEmpty(templateLangResolverListClassNameStr)) {
                this.templateLangResolverList.clear();
                String[] classNameLangResolverArray = templateLangResolverListClassNameStr.split("\\,\\\\");
                for (String classNameLangResolver : classNameLangResolverArray) {
                    try {
                        this.templateLangResolverList.add((TemplateLangResolver) Class.forName(classNameLangResolver).getDeclaredConstructor(this.getClass()).newInstance(this));
                    } catch (NoSuchMethodException e) {
                        throw new CodeConfigException(classNameLangResolver+"类文件构造函数异常");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        throw new CodeConfigException("配置文件:"+CONFIG_TEMPLATE_RESOLVERS+"配置项,类"+classNameLangResolver+"未找到");
                    } catch (ReflectiveOperationException e) {
                        throw new CodeConfigException(classNameLangResolver+"类文件对象实例化异常");
                    }
                }
                this.templateLangResolverList.add(new ToolTemplateLangResolver(this));
            }
        }
    }

    @Override
    public String analysisBody(Set<String> variableSet, String targetObjectKey, Object targetObject, String body) {
        Map<String, Object> templateVariableMap = new HashMap<>(10);
        for (String str : variableSet) {
            boolean isExclude = excludeCheckTemplateVariableKey(str);
            if(null==targetObject || Utils.isEmpty(str)){
                templateVariableMap.put(str,str.trim());
                continue;
            }
            if (!isExclude && !str.split("\\.")[0].equals(targetObjectKey)) {
                continue;
            }
            if (!isExclude) {
                templateVariableMap.put(str, Utils.getObjectFieldValue(str, targetObject));
            } else {
                for (Pattern pattern : getAllExcludeVariablePatten()) {
                    Matcher matcher = pattern.matcher(str);
                    if (matcher.find()) {
                        String title;
                        try {
                            title = matcher.group("title");
                        } catch (IllegalArgumentException e) {
                            break;
                        }
                        if (!title.contains("==")) {
                            Object titleValue = Utils.getObjectFieldValue(title, targetObject);
                            if(null!=titleValue) {
                                String result = "";
                                if (title.contains("!")) {
                                    result = String.valueOf(!(Boolean) titleValue);
                                } else {
                                    result = String.valueOf(titleValue);
                                }
                                templateVariableMap.put(str, str.replaceAll(title, result));
                            }
                        } else {
                            try {
                                String[] array = title.split("==");
                                Object titleValue = Utils.getObjectFieldValue(array[0], targetObject);
                                if (String.valueOf(titleValue).equals(array[1])) {
                                    templateVariableMap.put(str, str.replaceAll(title, String.valueOf(true)));
                                } else {
                                    templateVariableMap.put(str, str.replaceAll(title, String.valueOf(false)));
                                }
                            } catch (Exception e) {
                                String[] array = title.split("==");
                                Object titleValue = Utils.getObjectFieldValue(array[1], targetObject);
                                if (String.valueOf(titleValue).equals(array[0])) {
                                    templateVariableMap.put(str, str.replaceAll(title, String.valueOf(true)));
                                } else {
                                    templateVariableMap.put(str, str.replaceAll(title, String.valueOf(false)));
                                }
                            }
                        }
                    }
                }
            }
        }
        return this.replace(templateVariableMap, body);
    }


    /**
     * 获取字符串中模板变量{{}}中的值
     *
     * @param srcStr srcStr
     * @return
     */
    @Override
    public Set<String> getTemplateVariableKey(String srcStr) {
        Matcher matcher = TEMPLATE_VARIABLE_KEY_PATTERN.matcher(srcStr);
        Set<String> result = new HashSet<>(10);
        while (matcher.find()) {
            String key = matcher.group(TEMPLATE_VARIABLE_KEY);
            //过滤掉工具解析
            if (!ToolTemplateLangResolver.TEMPLATE_GRAMMAR_PATTERN_SUFFIX.matcher(key).find()) {
                result.add(key);
            }
        }
        return result;
    }

    /**
     * 获取字符串中模板变量{{}}中的值 包含tool工具当中的变量
     * @param srcStr srcStr
     * @return 获取字符串中模板变量{{}}中的值
     */
    public Set<String> getTemplateVariableKeyIncludeTool(String srcStr) {
        Matcher matcher = TEMPLATE_VARIABLE_KEY_PATTERN.matcher(srcStr);
        Set<String> result = new HashSet<>(10);
        while (matcher.find()) {
            String key = matcher.group(TEMPLATE_VARIABLE_KEY);
            //过滤掉工具解析
            if (!ToolTemplateLangResolver.TEMPLATE_GRAMMAR_PATTERN_SUFFIX.matcher(key).find()) {
                result.add(key);
            }else{
                getToolTemplateLangResolver()
                        .ifPresent(s-> result.addAll(s.getToolTemplateVariableKey(key)));
            }
        }
        return result;
    }

    public Optional<ToolTemplateLangResolver> getToolTemplateLangResolver(){
        return getTemplateLangResolverList().stream().filter(s->s instanceof ToolTemplateLangResolver)
                .map(s->(ToolTemplateLangResolver)s)
                .findFirst();
    }

    /**
     * 校验模板变量中的值是否需要排除有可能是
     *
     * @param key key
     * @return
     */
    public boolean excludeCheckTemplateVariableKey(String key) {
        for (Pattern pattern : getAllExcludeVariablePatten()) {
            if (pattern.matcher(key).find()) {
                return true;
            }
        }
        return false;
    }

    public Set<Pattern> getAllExcludeVariablePatten() {
        Set<Pattern> excludeVariablePattenSet = new HashSet<>();
        templateLangResolverList.forEach(item -> excludeVariablePattenSet.addAll(item.getExcludeVariablePatten()));
        return excludeVariablePattenSet;
    }

   /**
     * 替换掉 replaceTargetS 中的所有 在replaceKeyValue 能够找到的记录
     *
     * @param replaceKeyValue 存储的替换表
     * @param replaceTargetS  替换目标
     */
    @Override
    public String replace(Map<String, Object> replaceKeyValue, String replaceTargetS) {
        for (Map.Entry<String, Object> entryReplace : replaceKeyValue.entrySet()) {
            String key = entryReplace.getKey();
            Object value = entryReplace.getValue();
            String tempRule = "(" + TEMPLATE_VARIABLE_PREFIX + ")(" + key + ")(" + TEMPLATE_VARIABLE_SUFFIX + ")";
            boolean isExclude = excludeCheckTemplateVariableKey(key);
            if (isExclude) {
                replaceTargetS = replaceTargetS.replaceAll(key, String.valueOf(value));
            } else {
                replaceTargetS = replaceTargetS.replaceAll(tempRule, String.valueOf(value));
            }
        }
        return replaceTargetS;
    }

    /**
     * 替换 source当中的变量key
     * 例如 source=这是上层循环*{column.name}*
     * srcVariable=column.name
     * targetVariable=column2.name
     * 结果将等于 这是上层循环*{column2.name}*
     * @param source source
     * @param srcVariable srcVariable
     * @param targetVariable targetVariable
     * @return
     */
    public String replaceVariable(String source,String srcVariable,String targetVariable){
        return StrUtil.replace(source, doBuildCompleteVariable(srcVariable), s-> doBuildCompleteVariable(targetVariable));
    }

    /**
     * 替换 source当中的变量key当中的前缀
     * 例如 source=这是上层循环*{column.name}*
     * srcVariable=column.name
     * targetVariable=column2
     * 结果将等于 这是上层循环*{column2.name}*
     * @param source source
     * @param srcVariable srcVariable
     * @param targetVariable targetVariable
     * @return
     */
    public String replaceFirstVariable(String source,String srcVariable,String targetVariable){
        String variableSuffix= Stream.of(srcVariable.split("\\.")).skip(1)
                .collect(Collectors.joining(DOT));
        return StrUtil.replace(source, doBuildCompleteVariable(srcVariable),
                s-> doBuildCompleteVariable(targetVariable+ DOT+variableSuffix));
    }

    private static String doBuildCompleteVariable(String variableKey) {
        return StrUtil.format("{}{}{}", TEMPLATE_VARIABLE_PREFIX, variableKey, TEMPLATE_VARIABLE_SUFFIX);
    }

    public static String getTemplateVariableFormat(String str){
        return TEMPLATE_VARIABLE_PREFIX+str+TEMPLATE_VARIABLE_SUFFIX;
    }

}
