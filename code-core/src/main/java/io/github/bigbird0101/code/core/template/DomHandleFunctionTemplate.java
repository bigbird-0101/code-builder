package io.github.bigbird0101.code.core.template;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.annotation.JSONType;
import io.github.bigbird0101.code.core.cache.CacheKey;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.config.aware.EnvironmentAware;
import io.github.bigbird0101.code.core.domain.TemplateFileClassInfo;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.template.languagenode.*;
import io.github.bigbird0101.code.exception.TemplateResolveException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 15:55:26
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DomHandleFunctionTemplate extends DefaultHandleFunctionTemplate implements EnvironmentAware {
    private Environment environment;
    public DomHandleFunctionTemplate() {
        this.setTemplateResolver(new SimpleTemplateResolver());
    }

    @Override
    public void refresh() {
        if(null!= getTemplateResource()) {
            try {
                DomScriptCodeNodeBuilder domScriptCodeNodeBuilder=new DomScriptCodeNodeBuilder(XmlUtil.readXML(
                        getTemplateResource().getInputStream()));
                CodeNode source = domScriptCodeNodeBuilder.parse();
                this.templateFileClassInfoNoResolve = new TemplateFileClassInfo(getPrefix(source), getSuffix(source),
                        getFunctionS(source));
            } catch (FileNotFoundException e) {
                throw new CodeConfigException(e);
            } catch (UtilException | IOException ed) {
                throw new TemplateResolveException("dom {} template Resolve error {}",getTemplateName(),ed.getMessage());
            }
        }
        resolverResultCache.clear();
        deepClearCache();
        refreshed=true;
    }

    @Override
    public boolean doMatch(String content){
        return ReUtil.isMatch(DOM_MATCH_RULE,content);
    }

    @Override
    public int getOrder() {
        return 500;
    }

    @Override
    protected TemplateFileClassInfo doBuildTemplateResultCache(CacheKey cacheKey, Map<String, Object> dataModel) {
        return super.doBuildTemplateResultCache(cacheKey, dataModel);
    }

    private DynamicCodeNodeContext getCodeNodeContext() {
        return new DynamicCodeNodeContext(new HashMap<>(),environment);
    }

    private Map<String, String> getFunctionS(CodeNode source) {
        Map<String,String> result=new HashMap<>();
        if(source instanceof MixCodeNode){
            MixCodeNode codeSource= (MixCodeNode) source;
            final CodeNode codeNode = codeSource.getContents().get(0);
            if(codeNode instanceof TemplateCodeNode){
                TemplateCodeNode templateCodeNode= (TemplateCodeNode) codeNode;
                final CodeNode contents = templateCodeNode.getCodeNode();
                if(contents instanceof MixCodeNode){
                    MixCodeNode mixContent= (MixCodeNode) contents;
                    final List<FunctionCodeNode> functionCodeNodes = mixContent.getContents().stream()
                            .filter(s -> (s instanceof FunctionCodeNode))
                            .map(s -> (FunctionCodeNode) s)
                            .collect(Collectors.toList());
                    for (FunctionCodeNode functionCodeNode : functionCodeNodes) {
                        final DynamicCodeNodeContext codeNodeContext = getCodeNodeContext();
                        functionCodeNode.apply(codeNodeContext);
                        final String code = codeNodeContext.getCode();
                        final String name = functionCodeNode.getName();
                        result.put(name,code);
                    }
                }
            }
        }
        return result;
    }

    private String getSuffix(CodeNode source) {
        final DynamicCodeNodeContext codeNodeContext = getCodeNodeContext();
        if(source instanceof MixCodeNode){
            MixCodeNode codeSource= (MixCodeNode) source;
            final CodeNode codeNode = codeSource.getContents().get(0);
            if(codeNode instanceof TemplateCodeNode){
                TemplateCodeNode templateCodeNode= (TemplateCodeNode) codeNode;
                final CodeNode contents = templateCodeNode.getCodeNode();
                if(contents instanceof MixCodeNode){
                    MixCodeNode mixContent= (MixCodeNode) contents;
                    final CodeNode suffix = mixContent.getContents().stream()
                            .filter(s ->(s instanceof SuffixCodeNode))
                            .map(s->(SuffixCodeNode)s)
                            .findFirst()
                            .orElse(new SuffixCodeNode(new StaticTextCodeNode("")));
                    suffix.apply(codeNodeContext);
                    return codeNodeContext.getCode();
                }
            }
        }
        throw new TemplateResolveException("not support {} code node,except code node is mix",source.getClass().getSimpleName());
    }

    private String getPrefix(CodeNode source) {
        final DynamicCodeNodeContext codeNodeContext = getCodeNodeContext();
        if(source instanceof MixCodeNode){
            MixCodeNode codeSource= (MixCodeNode) source;
            final CodeNode codeNode = codeSource.getContents().get(0);
            if(codeNode instanceof TemplateCodeNode){
                TemplateCodeNode templateCodeNode= (TemplateCodeNode) codeNode;
                final CodeNode contents = templateCodeNode.getCodeNode();
                if(contents instanceof MixCodeNode){
                    MixCodeNode mixContent= (MixCodeNode) contents;
                    final CodeNode prefix = mixContent.getContents().stream()
                            .filter(s -> (s instanceof PrefixCodeNode))
                            .map(s->(PrefixCodeNode)s)
                            .findFirst()
                            .orElse(new PrefixCodeNode(new StaticTextCodeNode("")));
                    prefix.apply(codeNodeContext);
                    return codeNodeContext.getCode();
                }
            }
        }
        throw new TemplateResolveException("not support {} code node,except code node is mix",source.getClass().getSimpleName());
    }

    @Override
    public TemplateResolver getTemplateResolver() {
        return super.getTemplateResolver();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }
}
