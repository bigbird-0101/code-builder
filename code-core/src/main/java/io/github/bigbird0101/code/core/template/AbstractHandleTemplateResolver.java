package io.github.bigbird0101.code.core.template;

import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.util.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author pengpeng_fu@infinova.com.cn
 * @date 2024-01-02 17:16
 */
public abstract class AbstractHandleTemplateResolver extends AbstractTemplateResolver{
    /**
     * 方法名之间的标识
     */
    protected static final String FUNCTION_NAME_BETWEEN_SPLIT = "$";
    /**
     * 整个方法之间的标识
     */
    public static final String FUNCTION_BODY_BETWEEN_SPLIT = "function";

    /**
     * 整个模板前缀的标识
     */
    public static final String TEMPLATE_PREFIX_SPLIT = "prefix";

    /**
     * 整个模板后缀的标识
     */
    public static final String TEMPLATE_SUFFIX_SPLIT = "suffix";
    /**
     * 方法名 pattern group
     */
    public static final String TEMPLATE_FUNCTION_NAME = "functionName";
    /**
     * 获取方法名的正则
     */
    protected static final Pattern TEMPLATE_FUNCTION_NAME_PATTERN = Pattern.compile("(?<=\\" + FUNCTION_NAME_BETWEEN_SPLIT + ")(?<"+TEMPLATE_FUNCTION_NAME+">.*?)(?=\\" + FUNCTION_NAME_BETWEEN_SPLIT + ")");

    /**
     * 获取方法体的正则
     */
    protected static final Pattern TEMPLATE_FUNCTION_BODY_PATTERN = Pattern.compile("(" + TEMPLATE_VARIABLE_PREFIX + "\\s*?" + FUNCTION_BODY_BETWEEN_SPLIT + "\\s*?" + TEMPLATE_VARIABLE_SUFFIX + ")(?<title>.*?)(" + TEMPLATE_VARIABLE_PREFIX + "\\s*?/" + FUNCTION_BODY_BETWEEN_SPLIT + "\\s*?" + TEMPLATE_VARIABLE_SUFFIX + ")", Pattern.DOTALL);

    /**
     * 获取前缀的正则
     */
    protected static final Pattern TEMPLATE_PREFIX_PATTERN = Pattern.compile("(?<=" + TEMPLATE_VARIABLE_PREFIX + TEMPLATE_PREFIX_SPLIT + TEMPLATE_VARIABLE_SUFFIX + ")(.*)(?=" + TEMPLATE_VARIABLE_PREFIX + ".*/" + TEMPLATE_PREFIX_SPLIT + ".*" + TEMPLATE_VARIABLE_SUFFIX + ")", Pattern.DOTALL);

    /**
     * 获取后缀的正则
     */
    protected static final Pattern TEMPLATE_SUFFIX_PATTERN = Pattern.compile("(?<=" + TEMPLATE_VARIABLE_PREFIX + TEMPLATE_SUFFIX_SPLIT + TEMPLATE_VARIABLE_SUFFIX + ")(.*)(?=" + TEMPLATE_VARIABLE_PREFIX + ".*/" + TEMPLATE_SUFFIX_SPLIT + ".*" + TEMPLATE_VARIABLE_SUFFIX + ")", Pattern.DOTALL);

    /**
     * 方法名的前缀
     */
    protected static final Pattern TEMPLATE_FUNCTION_NAME_PREFIX_SUFFIX_PATTERN = Pattern.compile("(\\s*(?<bodyPrefix>.*?)" + TEMPLATE_VARIABLE_PREFIX+")(.*)(?="+TEMPLATE_VARIABLE_SUFFIX+"(?<bodySuffix>.*)\\s*)", Pattern.DOTALL);

    public AbstractHandleTemplateResolver() throws CodeConfigException {
    }

    public AbstractHandleTemplateResolver(Environment environment) throws CodeConfigException {
        super(environment);
    }

    /**
     * 获取模板内容的后缀
     *
     * @param templateContent 模板内容
     * @return 模板内容的后缀
     */
    public String getSuffix(String templateContent) {
        Matcher matcher = TEMPLATE_SUFFIX_PATTERN.matcher(templateContent);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    /**
     * 获取模板内容的前缀
     *
     * @param templateContent 模板内容
     * @return 模板内容的后缀
     */
    public String getPrefix(String templateContent) {
        Matcher matcher = TEMPLATE_PREFIX_PATTERN.matcher(templateContent);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    public String getNoResolverFunctionName(String srcFunctionName) {
        Matcher matcher=TEMPLATE_FUNCTION_NAME_PREFIX_SUFFIX_PATTERN.matcher(srcFunctionName);
        if(matcher.find()){
            String prefix=matcher.group("bodyPrefix");
            String suffix=matcher.group("bodySuffix");
            return prefix+suffix;
        }else{
            return srcFunctionName;
        }
    }

    public Map<String, String> getFunctionS(String templateContent) {
        Matcher matcher = TEMPLATE_FUNCTION_BODY_PATTERN.matcher(templateContent);
        Map<String, String> functions = new LinkedHashMap<>(10);
        while (matcher.find()) {
            String group = matcher.group("title");
            String functionName = getTemplateFunctionName(group);
            if (!Utils.isEmpty(functionName)) {
                functions.put(functionName, group);
            }
        }
        return functions;
    }

    public String getTemplateFunctionName(String src){
        Matcher functionNameMather = TEMPLATE_FUNCTION_NAME_PATTERN.matcher(src);
        return functionNameMather.find() ? functionNameMather.group() : "";
    }
}
