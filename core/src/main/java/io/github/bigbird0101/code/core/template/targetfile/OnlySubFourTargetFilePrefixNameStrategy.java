package io.github.bigbird0101.code.core.template.targetfile;

import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.util.Utils;

/**
 * 截断前面4个字符
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 19:00
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
    public String prefixStrategy(Template template, String srcSource) {
        return Utils.getFileNameByPath(srcSource.substring(4),"\\_");
    }
}
