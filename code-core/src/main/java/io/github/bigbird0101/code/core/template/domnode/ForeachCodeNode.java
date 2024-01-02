package io.github.bigbird0101.code.core.template.domnode;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.template.SimpleTemplateResolver;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import io.github.bigbird0101.code.util.Utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-10-01 21:50:45
 */
public class ForeachCodeNode implements CodeNode{
    private static final SimpleTemplateResolver SIMPLE_TEMPLATE_RESOLVER =SimpleTemplateResolver.getInstance();
    public static final String ITEM_INDEX_PREFIX="_foreach_";
    private final CodeNode contents;

    private final String separator;

    private final String collectionExpression;

    private final String item;

    public ForeachCodeNode(CodeNode contents, String separator, String collectionExpression, String item) {
        this.contents = contents;
        this.separator = separator;
        this.collectionExpression = collectionExpression;
        this.item = item;
    }

    @Override
    public boolean apply(CodeNodeContext context) {
        Object temp= Utils.getTargetObject(context.getTemplateVariable(),collectionExpression);
        if(!(temp instanceof Collection)){
            throw new TemplateResolveException("Foreach collectionExpression {} target object is not collection",
                    temp);
        }
        Collection<?> collection= (Collection<?>) temp;
        int a=0;
        for(Object o:collection){
            CodeNodeContext oldContext = context;
            if(0==a||null==separator) {
                context=new PrefixedContext(context, "");
            }else{
                context = new PrefixedContext(context, separator);
            }
            String uniqueNumber= IdUtil.randomUUID();
            applyItem(context,o,uniqueNumber);
            contents.apply(new FilteredDynamicContext(context, item, uniqueNumber));
            context=oldContext;
            a++;
        }
        return true;
    }

    private void applyItem(CodeNodeContext context, Object o, String uniqueNumber) {
        context.getTemplateVariable().put(item,o);
        context.getTemplateVariable().put(itemizeItemStart(item,uniqueNumber),o);
    }

    private static String itemizeItemStart(String item, String uniqueNumber) {
        return ITEM_INDEX_PREFIX+item+StrUtil.UNDERLINE+uniqueNumber;
    }

    private static boolean itemizeItemStart(String itemIndex) {
        return itemIndex.startsWith(ITEM_INDEX_PREFIX);
    }
    private static final class PrefixedContext implements CodeNodeContext{
        private final CodeNodeContext delegate;
        private final String prefix;

        private boolean prefixApplied;

        private PrefixedContext(CodeNodeContext delegate, String prefix) {
            this.delegate = delegate;
            this.prefix = prefix;
            this.prefixApplied=false;
        }


        @Override
        public String getCode() {
            return delegate.getCode();
        }

        @Override
        public void appendCode(String code) {
            if(!prefixApplied&& StrUtil.isNotBlank(code)){
                delegate.appendCode(prefix);
                prefixApplied=true;
            }
            delegate.appendCode(code);
        }

        @Override
        public Map<String, Object> getTemplateVariable() {
            return delegate.getTemplateVariable();
        }
    }

    private static final class FilteredDynamicContext implements CodeNodeContext {
        private final CodeNodeContext delegate;
        private final String item;
        private final String uniqueNumber;

        private FilteredDynamicContext(CodeNodeContext context, String item, String uniqueNumber) {
            this.delegate = context;
            this.item = item;
            this.uniqueNumber = uniqueNumber;
        }

        @Override
        public String getCode() {
            return delegate.getCode();
        }

        @Override
        public void appendCode(String code) {
            final Set<String> templateVariableKey = SIMPLE_TEMPLATE_RESOLVER.getTemplateVariableKeyIncludeTool(code);
            String result=code;
            for (String templateVariable : templateVariableKey) {
                if(!itemizeItemStart(templateVariable)&&templateVariable.startsWith(item)) {
                    result = SIMPLE_TEMPLATE_RESOLVER.replaceFirstVariable(result, templateVariable, itemizeItemStart(item, uniqueNumber));
                }
            }
            delegate.appendCode(result);
        }

        @Override
        public Map<String, Object> getTemplateVariable() {
            return delegate.getTemplateVariable();
        }
    }
}
