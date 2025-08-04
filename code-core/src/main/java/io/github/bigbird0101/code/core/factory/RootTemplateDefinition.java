package io.github.bigbird0101.code.core.factory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import io.github.bigbird0101.code.core.template.AbstractHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.targetfile.PatternTargetFilePrefixNameStrategy;
import io.github.bigbird0101.code.core.template.targetfile.TargetFilePrefixNameStrategy;
import io.github.bigbird0101.code.core.template.targetfile.TemplateFilePrefixNameStrategyFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;

/**
 * @author fpp
 */
public class RootTemplateDefinition extends AbstractTemplateDefinition {

    @JSONField(alternateNames = {"fileSuffixName"})
    private String targetFileSuffixName;

    private Boolean isHandleFunction;

    @JSONField(deserializeUsing = RootTemplateDefinitionDeserializer.class, serializeUsing = RootTemplateDefinitionDeserializer.class, alternateNames = {"filePrefixNameStrategy",
            "targetFilePrefixNameStrategy"}, name = "filePrefixNameStrategy")
    private TargetFilePrefixNameStrategy targetFilePrefixNameStrategy;

    @JSONField(defaultValue = "[]")
    private LinkedHashSet<String> dependTemplates;


    public RootTemplateDefinition() {
    }


    @Override
    public String getTargetFileSuffixName() {
        return targetFileSuffixName;
    }

    public void setTargetFileSuffixName(String targetFileSuffixName) {
        this.targetFileSuffixName = targetFileSuffixName;
    }

    @Override
    public boolean isHandleFunction() {
        return Optional.ofNullable(isHandleFunction).orElse(AbstractHandleFunctionTemplate.class
                .isAssignableFrom(getTemplateClass()));
    }

    public void setDependTemplates(LinkedHashSet<String> dependTemplates) {
        this.dependTemplates = dependTemplates;
    }

    @Override
    public LinkedHashSet<String> getDependTemplates() {
        return dependTemplates;
    }


    public void setHandleFunction(Boolean handleFunction) {
        isHandleFunction = handleFunction;
    }

    @Override
    public TargetFilePrefixNameStrategy getTargetFilePrefixNameStrategy() {
        return targetFilePrefixNameStrategy;
    }

    public void setTargetFilePrefixNameStrategy(TargetFilePrefixNameStrategy targetFilePrefixNameStrategy) {
        this.targetFilePrefixNameStrategy = targetFilePrefixNameStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RootTemplateDefinition that = (RootTemplateDefinition) o;
        return isHandleFunction.equals(that.isHandleFunction) &&
                targetFileSuffixName.equals(that.targetFileSuffixName) &&
                targetFilePrefixNameStrategy.equals(that.targetFilePrefixNameStrategy) &&
                dependTemplates.equals(that.dependTemplates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), targetFileSuffixName, isHandleFunction, targetFilePrefixNameStrategy, dependTemplates);
    }

    public static class RootTemplateDefinitionDeserializer implements ObjectDeserializer, ObjectSerializer {
        private static final Logger logger = LogManager.getLogger(RootTemplateDefinitionDeserializer.class);

        TemplateFilePrefixNameStrategyFactory templateFilePrefixNameStrategyFactory = new TemplateFilePrefixNameStrategyFactory();

        @Override
        @SuppressWarnings("unchecked")
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            JSONObject jsonObject = parser.parseObject();
            try {
                int value = jsonObject.getInteger("value");
                String pattern = jsonObject.getString("pattern");
                TargetFilePrefixNameStrategy filePrefixNameStrategy = templateFilePrefixNameStrategyFactory.getTemplateFilePrefixNameStrategy(value);
                if (filePrefixNameStrategy instanceof PatternTargetFilePrefixNameStrategy) {
                    ((PatternTargetFilePrefixNameStrategy) filePrefixNameStrategy).setPattern(pattern);
                }
                return (T) filePrefixNameStrategy;
            } catch (Exception e) {
                logger.error("RootTemplateDefinitionDeserializer error {},{},{}", e, e.getMessage(), jsonObject);
                return null;
            }
        }

        @Override
        public int getFastMatchToken() {
            return 0;
        }

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            JSONObject jsonObject = new JSONObject();
            TargetFilePrefixNameStrategy targetFilePrefixNameStrategy = (TargetFilePrefixNameStrategy) object;
            if (null != targetFilePrefixNameStrategy) {
                int typeValue = targetFilePrefixNameStrategy.getTypeValue();
                jsonObject.put("value", typeValue);
                if (targetFilePrefixNameStrategy instanceof PatternTargetFilePrefixNameStrategy patternTemplateFilePrefixNameStrategy) {
                    jsonObject.put("pattern", patternTemplateFilePrefixNameStrategy.getPattern());
                }
            }
            serializer.write(jsonObject);
        }
    }
}
