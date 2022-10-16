package io.github.bigbird0101.code.core.template.targetfile;

import io.github.bigbird0101.code.core.template.Template;

import java.io.Serializable;

/**
 * 模板最终生成文件的文件前缀名命名策略
 * @author fpp
 * @version 1.0
 */
public interface TargetFilePrefixNameStrategy extends Serializable {
    /**
     * 获取命名策略代表值
     * @return
     */
    int getTypeValue();
    /**
     * 命名策略
     * @param template 模板
     * @param srcSource 源资源 比如表名
     * @return
     */
    String prefixStrategy(Template template, String srcSource);

}
