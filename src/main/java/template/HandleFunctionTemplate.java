package main.java.template;

import main.java.common.Utils;
import main.java.config.CodeConfigException;
import main.java.config.ProjectFileConfig;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * 需要对方法进行操作的模板
 * @author fpp
 * @version 1.0
 * @date 2020/6/9 17:44
 */
public abstract class HandleFunctionTemplate extends AbstractTemplate {
    protected TemplateFileClassInfo templateFileClassInfo;

    protected ResolverStrategy resolverStrategy;

    public HandleFunctionTemplate(String templeFileName) throws IOException, CodeConfigException {
        super(templeFileName);
        refresh(templeFileName);
    }


    public HandleFunctionTemplate(String templeFileName, ProjectFileConfig projectFileConfig) throws IOException, CodeConfigException {
        super(templeFileName,projectFileConfig);
        refresh(templeFileName);
    }

    @Override
    public void refresh(String templeFileName) throws IOException {
        if(Utils.isNotEmpty(templeFileName)) {
            String templateFileContent = readTempleteFile(templeFileName);
            this.templateFileClassInfo = new TemplateFileClassInfo(getPrefix(templateFileContent), getSuffix(templateFileContent), getFunctionS(templateFileContent));
        }
    }


    /**
     * 获取根据模板最终生成的模板内容
     *
     * @param replaceKeyValue 需要替换的key和value
     * @return
     */
    @Override
    public String getTempleResult(Map<String, Object> replaceKeyValue) throws TemplateResolveException {
        String resultPrefix = this.templateFileClassInfo.getTemplateClassPrefix();
        String resultSuffix = this.templateFileClassInfo.getTemplateClassSuffix();
        Map<String, String> functionS = this.templateFileClassInfo.getFunctionS();

        Iterator<Map.Entry<String, String>> functionIterator = functionS.entrySet().iterator();
        //清除前缀和后缀的{{}}代码
        resultPrefix=getTemplateResolver().resolver(resultPrefix,replaceKeyValue);
        resultSuffix=getTemplateResolver().resolver(resultSuffix,replaceKeyValue);

        //清除方法名和方法体的{{}}代码
        Map<String, String> tempFunctionMap = new LinkedHashMap<>(functionS.size());
        while (functionIterator.hasNext()) {
            Map.Entry<String, String> entry = functionIterator.next();
            String functionName = entry.getKey();
            String functionBody = entry.getValue().replaceAll("\\"+AbstractTemplateResolver.FUNCTION_NAME_BETWEEN_SPLIT,"");
            functionName=getNoResolverFunctionName(functionName);
            functionBody=getTemplateResolver().resolver(functionBody,replaceKeyValue);
            if (!tempFunctionMap.containsKey(functionName)) {
                tempFunctionMap.put(functionName, functionBody);
            }
        }

        this.templateFileClassInfo.setTemplateClassPrefix(resultPrefix);
        this.templateFileClassInfo.setTemplateClassSuffix(resultSuffix);
        this.templateFileClassInfo.setFunctionS(tempFunctionMap);

        //解析策略
        if(null!=resolverStrategy) {
            resolverStrategy.resolverStrategy(this.templateFileClassInfo);
        }

        tempFunctionMap =this.templateFileClassInfo.getFunctionS();
        StringBuilder functionStr = new StringBuilder();
        tempFunctionMap.forEach((k, v) -> {
                functionStr.append(v);
        });

        return this.templateFileClassInfo.getTemplateClassPrefix() + functionStr.toString() + this.templateFileClassInfo.getTemplateClassSuffix();
    }




    /**
     * 获取模板内容的后缀
     *
     * @param templateContent 模板内容
     * @return 模板内容的后缀
     */
    public String getSuffix(String templateContent) {
        Matcher matcher = AbstractTemplateResolver.templateSuffixPattern.matcher(templateContent);
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
        Matcher matcher = AbstractTemplateResolver.templatePefixPattern.matcher(templateContent);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }


    /**
     * 获取模板方法的内容
     *
     * @param templateContent 模板内容
     * @return 模板方法的内容
     */
    public Map<String, String> getFunctionS(String templateContent) {
        Matcher matcher = AbstractTemplateResolver.templateFunctionBodyPattern.matcher(templateContent);
        Map<String, String> functions = new LinkedHashMap<>(10);
        while (matcher.find()) {
            String group = matcher.group("title");
            Matcher functionNameMather = AbstractTemplateResolver.templateFunctionNamePattern.matcher(group);
            String functionName = functionNameMather.find() ? functionNameMather.group() : "";
            if (!Utils.isEmpty(functionName)) {
                functions.put(functionName, group);
            }
        }
        return functions;
    }

    /**
     * 设置模板解析策略
     * @param resolverStrategy 模板解析策略
     */
    public abstract void setResolverStrategy(ResolverStrategy resolverStrategy);

    /**
     * 获取所有的方法名
     * @return
     */
    public Set<String> getTemplateFunctionNameS() throws TemplateResolveException {
        Set<String> functionNameS=new HashSet<>(templateFileClassInfo.getFunctionS().keySet().size());
        for(String functionName:templateFileClassInfo.getFunctionS().keySet()){
            functionNameS.add(getNoResolverFunctionName(functionName));
        }
        return functionNameS;
    }

    public String getNoResolverFunctionName(String srcFunctionName) throws TemplateResolveException {
        Matcher matcher=AbstractTemplateResolver.templateFunctionNamePefixSuffixPattern.matcher(srcFunctionName);
        if(matcher.find()){
            String prefix=matcher.group("bodyPrefix");
            String suffix=matcher.group("bodySuffix");
            return prefix+suffix;
        }else{
            throw new TemplateResolveException("模板方法名解析异常,模板方法名为:"+srcFunctionName);
        }
    }
}
