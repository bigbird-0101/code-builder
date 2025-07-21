package io.github.bigbird0101.code.core.template;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.annotation.JSONType;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.config.aware.EnvironmentAware;
import io.github.bigbird0101.code.core.domain.TemplateFileClassInfo;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.template.domnode.CodeNode;
import io.github.bigbird0101.code.core.template.domnode.DomScriptCodeNodeBuilder;
import io.github.bigbird0101.code.core.template.domnode.DynamicCodeNodeContext;
import io.github.bigbird0101.code.core.template.domnode.FunctionCodeNode;
import io.github.bigbird0101.code.core.template.domnode.MixCodeNode;
import io.github.bigbird0101.code.core.template.domnode.PrefixCodeNode;
import io.github.bigbird0101.code.core.template.domnode.StaticTextCodeNode;
import io.github.bigbird0101.code.core.template.domnode.SuffixCodeNode;
import io.github.bigbird0101.code.core.template.domnode.TemplateCodeNode;
import io.github.bigbird0101.code.core.template.domnode.VarCodeNode;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 15:55:26
 */
@JSONType(serializer = AbstractTemplate.TemplateSerializer.class)
public class DomHandleFunctionTemplate extends DefaultHandleFunctionTemplate implements EnvironmentAware {
    private Environment environment;
    private Document context;
    public DomHandleFunctionTemplate() {
        this.setTemplateResolver(SimpleAbstractTemplateResolver.getInstance());
    }

    public DomHandleFunctionTemplate(URL url) {
        super(url);
        this.setTemplateResolver(SimpleAbstractTemplateResolver.getInstance());
    }

    @Override
    public void refresh() {
        if(null!= getTemplateResource()) {
            try {
                context = XmlUtil.readXML(getTemplateResource().getInputStream());
                Element rootDocument = context.getDocumentElement();
                Node prefixNode = rootDocument.getElementsByTagName("prefix").item(0);
                Node suffixNode = rootDocument.getElementsByTagName("suffix").item(0);
                NodeList functionNodes = rootDocument.getElementsByTagName("function");
                this.templateFileClassInfoNoResolve = new TemplateFileClassInfo(getPrefix(prefixNode), getSuffix(suffixNode),
                        getFunctionS(functionNodes));
            } catch (IOException e) {
                throw new CodeConfigException(e);
            } catch (UtilException ed) {
                Throwable rootCause = ExceptionUtil.getRootCause(ed);
                throw new TemplateResolveException("dom {} template Resolve error {},line:",getTemplateName(),rootCause.toString());
            }
        }
        resolverResultCache.clear();
        deepClearCache();
        refreshed=true;
    }

    private String getSuffix(Node suffixNode) {
        return Optional.ofNullable(suffixNode)
                .map(Node::getTextContent)
                .orElse(null);
    }


    private String getPrefix(Node prefixNode) {
        return Optional.ofNullable(prefixNode)
                .map(Node::getTextContent)
                .orElse(null);
    }


    private Map<String, String> getFunctionS(NodeList functionNodes) {
        if(null==functionNodes || 0==functionNodes.getLength()){
            return Collections.emptyMap();
        }
        int length = functionNodes.getLength();
        Map<String,String> result=new HashMap<>();
        for(int a=0;a<length;a++){
            Node item = functionNodes.item(a);
            NamedNodeMap attributes = item.getAttributes();
            Node name = attributes.getNamedItem("name");
            if (null == name) {
                throw new TemplateResolveException("this dom template {} index = {} no get function name ", getTemplateName(), a);
            }
            String textContent = item.getTextContent();
            result.put(name.getNodeValue(), textContent);
        }
        return result;
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
    protected TemplateFileClassInfo doBuildTemplateResultCache(Map<String, Object> dataModel) {
        TemplateFileClassInfo resultCache;
        DomScriptCodeNodeBuilder domScriptCodeNodeBuilder=new DomScriptCodeNodeBuilder(context);
        CodeNode source = domScriptCodeNodeBuilder.parse();
        doSetVar(source,dataModel);
        String resultPrefix = getPrefix(source,dataModel);
        String resultSuffix = getSuffix(source,dataModel);
        Map<String, String> functionS = getFunctionS(source,dataModel);
        resultCache = doResolverAll(dataModel, resultPrefix, resultSuffix, functionS);
        this.templateFileClassInfoResolved =resultCache;
        return resultCache;
    }

    private void doSetVar(CodeNode source, Map<String, Object> dataModel) {
        if (source instanceof MixCodeNode codeSource) {
            final CodeNode codeNode = codeSource.getContents().get(0);
            if (codeNode instanceof TemplateCodeNode templateCodeNode) {
                final CodeNode contents = templateCodeNode.getCodeNode();
                if (contents instanceof MixCodeNode mixContent) {
                    mixContent.getContents().stream()
                            .filter(s ->(s instanceof VarCodeNode))
                            .map(s->(VarCodeNode)s)
                            .forEach(s-> s.apply(getCodeNodeContext(dataModel)));
                }
            }
        }
    }

    private DynamicCodeNodeContext getCodeNodeContext(Map<String, Object> dataModel) {
        return new DynamicCodeNodeContext(dataModel,environment);
    }

    private Map<String, String> getFunctionS(CodeNode source, Map<String, Object> dataModel) {
        Map<String,String> result=new HashMap<>();
        if(source instanceof MixCodeNode){
            MixCodeNode codeSource= (MixCodeNode) source;
            final CodeNode codeNode = codeSource.getContents().get(0);
            if (codeNode instanceof TemplateCodeNode templateCodeNode) {
                final CodeNode contents = templateCodeNode.getCodeNode();
                if (contents instanceof MixCodeNode mixContent) {
                    final List<FunctionCodeNode> functionCodeNodes = mixContent.getContents().stream()
                            .filter(s -> (s instanceof FunctionCodeNode))
                            .map(s -> (FunctionCodeNode) s)
                            .collect(Collectors.toList());
                    for (FunctionCodeNode functionCodeNode : functionCodeNodes) {
                        final DynamicCodeNodeContext codeNodeContext = getCodeNodeContext(dataModel);
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

    private String getSuffix(CodeNode source, Map<String, Object> dataModel) {
        final DynamicCodeNodeContext codeNodeContext = getCodeNodeContext(dataModel);
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

    private String getPrefix(CodeNode source, Map<String, Object> dataModel) {
        final DynamicCodeNodeContext codeNodeContext = getCodeNodeContext(dataModel);
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
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }
}
