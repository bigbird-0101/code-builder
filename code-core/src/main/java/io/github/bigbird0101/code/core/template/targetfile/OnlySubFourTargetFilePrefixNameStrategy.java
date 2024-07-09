package io.github.bigbird0101.code.core.template.targetfile;

import io.github.bigbird0101.code.core.exception.CodeBuilderException;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.util.Utils;

import java.util.Map;

/**
 * 截断前面4个字符
 * @author fpp
 * @version 1.0
 */
public class OnlySubFourTargetFilePrefixNameStrategy implements TargetFilePrefixNameStrategy {
    private static final int TYPE_VALUE=2;

    /**
     * 获取命名策略代表值
     *
     * @return
     */
    @Override
    public int getTypeValue() {
        return TYPE_VALUE;
    }

    /**
     * 命名策略
     *
     * @param template  模板
     * @param srcSource 源资源 比如表名
     * @return
     */
    @Override
    public String prefixStrategy(Template template, String srcSource, Map<String,Object> dataModel) {
        try {
            return Utils.getFileNameByPath(srcSource.substring(4), "\\_");
        } catch (Exception e) {
            throw new CodeBuilderException(String.format("模版 %s 生成文件名异常", template.getTemplateName()), e);
        }
    }
}
