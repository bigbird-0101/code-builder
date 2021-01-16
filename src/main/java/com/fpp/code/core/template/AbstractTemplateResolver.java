package com.fpp.code.core.template;

import com.fpp.code.common.Utils;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 13:29
 */
public abstract class AbstractTemplateResolver implements TemplateResolver {

    private static Logger logger= LogManager.getLogger(AbstractTemplateResolver.class);

    protected static final String CONFIG_TEMPLATE_RESOLVERS="code.project.file.template-lang-resolver";

    /**
     * 方法名之间的标识
     */
    protected static final String FUNCTION_NAME_BETWEEN_SPLIT = "$";

    /**
     * 模板变量的前缀标识
     * 模板变量使用 {{变量名}}
     */
    protected static final String TEMPLATE_VARIABLE_PREFIX = "\\*\\{";

    /**
     * 模板变量的后缀标识
     * 模板变量使用 {{变量名}}
     */
    protected static final String TEMPLATE_VARIABLE_SUFFIX = "\\}\\*";

    /**
     * 整个方法之间的标识
     */
    protected static final String FUNCTION_BODY_BETWEEN_SPLIT = "function";

    /**
     * 整个模板前缀的标识
     */
    protected static final String TEMPLATE_PREFIX_SPLIT = "prefix";

    /**
     * 整个模板后缀的标识
     */
    protected static final String TEMPLATE_SUFFIX_SPLIT = "suffix";

    /**
     * 获取方法名的正则
     */
    protected static Pattern templateFunctionNamePattern = Pattern.compile("(?<=\\" + FUNCTION_NAME_BETWEEN_SPLIT + ")(.+?)(?=\\" + FUNCTION_NAME_BETWEEN_SPLIT + ")");

    /**
     * 获取模板变量{{}}的key值
     */
    protected static Pattern templateVariableKeyPattern = Pattern.compile("(?<=" + TEMPLATE_VARIABLE_PREFIX + ")(.+?)(?=" + TEMPLATE_VARIABLE_SUFFIX + ")");

    /**
     * 获取方法体的正则
     */
    protected static Pattern templateFunctionBodyPattern = Pattern.compile("(" + TEMPLATE_VARIABLE_PREFIX + "\\s*?" + FUNCTION_BODY_BETWEEN_SPLIT + "\\s*?" + TEMPLATE_VARIABLE_SUFFIX + ")(?<title>.*?)(" + TEMPLATE_VARIABLE_PREFIX + "\\s*?/" + FUNCTION_BODY_BETWEEN_SPLIT + "\\s*?" + TEMPLATE_VARIABLE_SUFFIX + ")", Pattern.DOTALL);

    /**
     * 获取前缀的正则
     */
    protected static Pattern templatePefixPattern = Pattern.compile("(?<=" + TEMPLATE_VARIABLE_PREFIX + TEMPLATE_PREFIX_SPLIT + TEMPLATE_VARIABLE_SUFFIX + ")(.*)(?=" + TEMPLATE_VARIABLE_PREFIX + ".*/" + TEMPLATE_PREFIX_SPLIT + ".*" + TEMPLATE_VARIABLE_SUFFIX + ")", Pattern.DOTALL);

    /**
     * 获取后缀的正则
     */
    protected static Pattern templateSuffixPattern = Pattern.compile("(?<=" + TEMPLATE_VARIABLE_PREFIX + TEMPLATE_SUFFIX_SPLIT + TEMPLATE_VARIABLE_SUFFIX + ")(.*)(?=" + TEMPLATE_VARIABLE_PREFIX + ".*/" + TEMPLATE_SUFFIX_SPLIT + ".*" + TEMPLATE_VARIABLE_SUFFIX + ")", Pattern.DOTALL);

    /**
     * 方法名的前缀
     */
    protected static Pattern templateFunctionNamePefixSuffixPattern = Pattern.compile("(\\s*(?<bodyPrefix>.*?)" + TEMPLATE_VARIABLE_PREFIX+")(.*)(?="+TEMPLATE_VARIABLE_SUFFIX+"(?<bodySuffix>.*)\\s*)", Pattern.DOTALL);


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
        this.templateLangResolverList=new ArrayList<>(16);
        ServiceLoader<TemplateLangResolver> serviceLoader=ServiceLoader.load(TemplateLangResolver.class);
        serviceLoader.forEach(item->{
            item.setTemplateResolver(this);
            this.templateLangResolverList.add(item);
        });
        if(templateLangResolverList.size()==0&&logger.isWarnEnabled()){
            logger.warn("template lang resolver load failed");
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
                this.templateLangResolverList.add(new ToolTemplateResolver(this));
            }
        }
    }

    @Override
    public String analysisBody(Set<String> variableSet, String targetObjectKey, Object targetObject, String body)throws TemplateResolveException {
        Map<String, Object> templateVariableKV = new HashMap<>(10);
        for (String str : variableSet) {
            boolean isExclude = excludeCheckTemplateVariableKey(str);
            if (!isExclude && !str.split("\\.")[0].equals(targetObjectKey)) {
                throw new TemplateResolveException(str + "属性在"+targetObject.getClass().getSimpleName()+"不存在");
            }
            if (!isExclude) {
                templateVariableKV.put(str, Utils.getObjectFieldValue(str, targetObject));
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
                            String result = "";
                            if (title.contains("!")) {
                                result = String.valueOf(!(Boolean) titleValue);
                            } else {
                                result = String.valueOf(titleValue);
                            }
                            templateVariableKV.put(str, str.replaceAll(title, result));
                        } else {
                            try {
                                String[] array = title.split("==");
                                Object titleValue = Utils.getObjectFieldValue(array[0], targetObject);
                                if (String.valueOf(titleValue).equals(array[1])) {
                                    templateVariableKV.put(str, str.replaceAll(title, String.valueOf(true)));
                                } else {
                                    templateVariableKV.put(str, str.replaceAll(title, String.valueOf(false)));
                                }
                            } catch (Exception e) {
                                String[] array = title.split("==");
                                Object titleValue = Utils.getObjectFieldValue(array[1], targetObject);
                                if (String.valueOf(titleValue).equals(array[0])) {
                                    templateVariableKV.put(str, str.replaceAll(title, String.valueOf(true)));
                                } else {
                                    templateVariableKV.put(str, str.replaceAll(title, String.valueOf(false)));
                                }
                            }
                        }
                    }
                }
            }
        }
        return this.replace(templateVariableKV, body);
    }


    /**
     * 获取字符串中模板变量{{}}中的值
     *
     * @param srcStr
     * @return
     */
    @Override
    public Set<String> getTemplateVariableKey(String srcStr) {
        Matcher matcher = AbstractTemplateResolver.templateVariableKeyPattern.matcher(srcStr);
        Set<String> result = new HashSet<>(10);
        while (matcher.find()) {
            String key = matcher.group();
            //过滤掉工具解析
            if (!ToolTemplateResolver.templateGrammarPatternSuffix.matcher(key).find()) {
                result.add(key);
            }
        }
        return result;
    }

    /**
     * 校验模板变量中的值是否需要排除有可能是
     *
     * @param key
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
        templateLangResolverList.forEach(item -> {
            excludeVariablePattenSet.addAll(item.getExcludeVariablePatten());
        });
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
        Iterator<Map.Entry<String, Object>> tempReplaceIterator = replaceKeyValue.entrySet().iterator();
        while (tempReplaceIterator.hasNext()) {
            Map.Entry<String, Object> entryReplace = tempReplaceIterator.next();
            String key = entryReplace.getKey();
            Object value = entryReplace.getValue();
            String tempRule = "(" + TEMPLATE_VARIABLE_PREFIX + ")(" + key + ")(" + TEMPLATE_VARIABLE_SUFFIX + ")";
            boolean isExclude=excludeCheckTemplateVariableKey(key);
            if(isExclude){
                replaceTargetS = replaceTargetS.replaceAll(key,String.valueOf(value));
            }else {
                replaceTargetS = replaceTargetS.replaceAll(tempRule, String.valueOf(value));
            }
        }
        return replaceTargetS;
    }


}
