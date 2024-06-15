package io.github.bigbird0101.code.core.template.domnode;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.spi.TypeBasedSPI;
import io.github.bigbird0101.code.core.spi.TypeBasedSPIServiceLoader;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import static io.github.bigbird0101.code.core.template.AbstractHandleAbstractTemplateResolver.FUNCTION_BODY_BETWEEN_SPLIT;
import static io.github.bigbird0101.code.core.template.AbstractHandleAbstractTemplateResolver.TEMPLATE_PREFIX_SPLIT;
import static io.github.bigbird0101.code.core.template.AbstractHandleAbstractTemplateResolver.TEMPLATE_SUFFIX_SPLIT;
import static java.util.stream.Collectors.toList;
import static org.w3c.dom.Node.CDATA_SECTION_NODE;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-04 11:12:10
 */
public class DomScriptCodeNodeBuilder implements CodeNodeBuilder{

    private final Node context;
    private static final Map<String,CodeNodeHandler> CODE_NODE_HANDLER_MAP =new HashMap<>();

    public DomScriptCodeNodeBuilder(Node context) {
        this.context = context;
        initHandler();
    }

    private void initHandler() {
        CODE_NODE_HANDLER_MAP.putAll(new CodeNodeHandlerTypeBasedSPIServiceLoader(CodeNodeHandler.class)
                .loadServiceMap(new Properties()));
    }

    @Override
    public CodeNode parse() {
        return parseNode(context);
    }

    private static MixCodeNode parseNode(Node documentElement) {
        List<CodeNode> list=new ArrayList<>();
        final NodeList anIf = documentElement.getChildNodes();
        for(int length=anIf.getLength(),b=0;b<length;b++){
            final Node item = anIf.item(b);
            final short nodeType = item.getNodeType();
            if(TEXT_NODE==nodeType||CDATA_SECTION_NODE==nodeType){
                final String nodeValue = item.getNodeValue();
                list.add(new StaticTextCodeNode(nodeValue));
            }else if(ELEMENT_NODE==nodeType){
                final String nodeName = item.getNodeName();
                final CodeNodeHandler codeNodeHandler = CODE_NODE_HANDLER_MAP.get(nodeName);
                if(null==codeNodeHandler){
                    throw new TemplateResolveException("{} tag not support language",nodeName);
                }
                codeNodeHandler.handleNode(item,list);
            }
        }
        return new MixCodeNode(list);
    }

    /**
     * 构建 具体的{@link CodeNode} 并且加入到整体的contents当中
     */
    public interface CodeNodeHandler extends TypeBasedSPI {
        /**
         * template节点
         */
        String TEMPLATE ="template";
        /**
         * 构建 具体的{@link CodeNode} 并且加入到整体的contents当中
         * @param node 需要解析的节点
         * @param contents 整个整体的contents 由CodeNode组成
         */
        void handleNode(Node node, List<CodeNode> contents);

        /**
         * 设置配置信息
         * @param properties properties
         */
        @Override
        default void init(Properties properties) {}

        /**
         * 获取NamedNodeMap 当中的属性 如果没有获取到 就返回默认值
         * @param namedNodeMap namedNodeMap
         * @param name name
         * @param defaultValue defaultValue
         * @return
         */
        default String getAttributeOrDefault(NamedNodeMap namedNodeMap, String name, String defaultValue){
            return Optional.ofNullable(namedNodeMap.getNamedItem(name))
                    .map(Node::getNodeValue).orElse(defaultValue);
        }

        /**
         * 获取NamedNodeMap 当中的属性 如果没有获取到 就抛出异常
         * @param namedNodeMap namedNodeMap
         * @param name name
         * @param errorMsg errorMsg
         * @return
         */
        default String getAttributeOrThrow(NamedNodeMap namedNodeMap, String name, String errorMsg){
            return Optional.ofNullable(namedNodeMap.getNamedItem(name))
                    .map(Node::getNodeValue).orElseThrow(()->new TemplateResolveException(errorMsg));
        }
    }


    public static class IfCodeNodeHandler implements CodeNodeHandler{
        public static final String V_IF = "v-if";
        @Override
        public void handleNode(Node node, List<CodeNode> contents) {
            final MixCodeNode mixCodeNode = parseNode(node);
            final String vIf = getAttributeOrThrow(node.getAttributes(),V_IF,"if not get v-if error");
            contents.add(new IfCodeNode(vIf, mixCodeNode));
        }

        @Override
        public String getType() {
            return "if";
        }
    }

    public static class ChooseCodeNodeHandler implements CodeNodeHandler{
        @Override
        public void handleNode(Node node, List<CodeNode> contents) {
            List<CodeNode> whenCodeNodes=new ArrayList<>();
            List<CodeNode> otherCodeNodes=new ArrayList<>();
            parseWhenCodeNodes(node,whenCodeNodes,otherCodeNodes);
            contents.add(new ChooseCodeNode(whenCodeNodes, otherCodeNodes.get(0)));
        }

        private void parseWhenCodeNodes(Node node, List<CodeNode> whenCodeNodes, List<CodeNode> otherCodeNodes) {
            final NodeList anIf = node.getChildNodes();
            for(int length=anIf.getLength(),b=0;b<length;b++){
                final Node item = anIf.item(b);
                final short nodeType = item.getNodeType();
                if(ELEMENT_NODE==nodeType){
                    final String nodeName = item.getNodeName();
                    final CodeNodeHandler codeNodeHandler = CODE_NODE_HANDLER_MAP.get(nodeName);
                    if(codeNodeHandler instanceof WhenCodeNodeHandler){
                        WhenCodeNodeHandler whenCodeNodeHandler= (WhenCodeNodeHandler) codeNodeHandler;
                        whenCodeNodeHandler.handleNode(item,whenCodeNodes);
                    }
                    if(codeNodeHandler instanceof OtherwiseCodeNodeHandler){
                        OtherwiseCodeNodeHandler otherwiseCodeNodeHandler= (OtherwiseCodeNodeHandler) codeNodeHandler;
                        otherwiseCodeNodeHandler.handleNode(item,otherCodeNodes);
                    }
                }
            }
        }

        @Override
        public String getType() {
            return "choose";
        }
    }

    public static class WhenCodeNodeHandler implements CodeNodeHandler{
        public static final String TEST = "test";
        @Override
        public void handleNode(Node node, List<CodeNode> contents) {
            final MixCodeNode mixCodeNode = parseNode(node);
            final String test = getAttributeOrThrow(node.getAttributes(), TEST,"Choose not get test error");
            contents.add(new IfCodeNode(test, mixCodeNode));
        }

        @Override
        public String getType() {
            return "when";
        }
    }

    public static class ForeachCodeNodeHandler implements CodeNodeHandler{
        public static final String IN_REGEX = " in ";
        public static final String V_FOR = "v-for";
        public static final String SEPARATOR = "separator";
        @Override
        public void handleNode(Node node, List<CodeNode> contents) {
            final MixCodeNode mixCodeNode = parseNode(node);
            final String separator = getAttributeOrDefault(node.getAttributes(),SEPARATOR,StrUtil.EMPTY);
            final String vFor = getAttributeOrThrow(node.getAttributes(),V_FOR,"foreach not get v-for error");
            final List<String> collect = Stream.of(vFor.split(IN_REGEX)).map(String::trim).collect(toList());
            if(!vFor.contains(IN_REGEX)||collect.size()!=2){
                throw new TemplateResolveException("foreach 语句 语法异常,未在v-for中以 in 隔开");
            }
            contents.add(new ForeachCodeNode(mixCodeNode,separator,collect.get(1),collect.get(0)));
        }

        @Override
        public String getType() {
            return "foreach";
        }
    }

    public static class OtherwiseCodeNodeHandler implements CodeNodeHandler{
        int a;
        @Override
        public void handleNode(Node node, List<CodeNode> contents) {
            final MixCodeNode mixCodeNode = parseNode(node);
            contents.add(mixCodeNode);
        }
        @Override
        public String getType() {
            return "otherwise";
        }

    }

    public static class PrefixCodeNodeHandler implements CodeNodeHandler{
        int a;
        @Override
        public void handleNode(Node node, List<CodeNode> contents) {
            final MixCodeNode mixCodeNode = parseNode(node);
            PrefixCodeNode prefixCodeNode=new PrefixCodeNode(mixCodeNode);
            contents.add(prefixCodeNode);
        }

        @Override
        public String getType() {
            return TEMPLATE_PREFIX_SPLIT;
        }

    }

    public static class SuffixCodeNodeHandler implements CodeNodeHandler{
        @Override
        public void handleNode(Node node, List<CodeNode> contents) {
            final MixCodeNode mixCodeNode = parseNode(node);
            SuffixCodeNode suffixCodeNode=new SuffixCodeNode(mixCodeNode);
            contents.add(suffixCodeNode);
        }

        @Override
        public String getType() {
            return TEMPLATE_SUFFIX_SPLIT;
        }
    }

    public static class FunctionCodeNodeHandler implements CodeNodeHandler{
        int a=0;
        @Override
        public void handleNode(Node node, List<CodeNode> contents) {
            final MixCodeNode mixCodeNode = parseNode(node);
            final String name = getAttributeOrDefault(node.getAttributes(), "name", "function_" + getNumber());
            FunctionCodeNode functionCodeNode=new FunctionCodeNode(name, mixCodeNode);
            contents.add(functionCodeNode);
        }

        private int getNumber() {
            return ++a;
        }

        @Override
        public String getType() {
            return FUNCTION_BODY_BETWEEN_SPLIT;
        }
    }

    public static class TemplateCodeNodeHandler implements CodeNodeHandler{

        @Override
        public void handleNode(Node node, List<CodeNode> contents) {
            final MixCodeNode mixCodeNode = parseNode(node);
            TemplateCodeNode templateCodeNode=new TemplateCodeNode(mixCodeNode);
            contents.add(templateCodeNode);
        }

        @Override
        public String getType() {
            return TEMPLATE;
        }
    }

    public static class VarCodeNodeHandler implements CodeNodeHandler{
        @Override
        public void handleNode(Node node, List<CodeNode> contents) {
            final String name = getAttributeOrDefault(node.getAttributes(),"name","var not get name error");
            final String value = getAttributeOrThrow(node.getAttributes(),"value","var not get value error");
            VarCodeNode varCodeNode=new VarCodeNode(name,value);
            contents.add(varCodeNode);
        }

        @Override
        public String getType() {
            return "var";
        }
    }

    /**
     * {@link CodeNodeHandler} code-spi loader
     */
    protected static class CodeNodeHandlerTypeBasedSPIServiceLoader extends TypeBasedSPIServiceLoader<CodeNodeHandler>{
        protected CodeNodeHandlerTypeBasedSPIServiceLoader(Class<CodeNodeHandler> classType) {
            super(classType);
        }
    }
}
