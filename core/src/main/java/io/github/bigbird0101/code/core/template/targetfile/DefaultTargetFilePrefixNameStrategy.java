package io.github.bigbird0101.code.core.template.targetfile;

import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.util.Utils;

/**
 * 默认模板最终生成文件的文件名命名策略
 * @author fpp
 * @version 1.0
 */
public class DefaultTargetFilePrefixNameStrategy implements TargetFilePrefixNameStrategy {
    private static final int TYPE_VALUE=1;

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
    public String prefixStrategy(Template template, String srcSource) {
        return Utils.getFileNameByPath(srcSource.substring(4),"\\_")+Utils.getFileNameByPath(template.getSrcPackage(),"\\/");
    }
}
