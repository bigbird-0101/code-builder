package com.fpp.code.core.template;

import com.fpp.code.common.Utils;

/**
 * 默认模板最终生成文件的文件名命名策略
 * @author fpp
 * @version 1.0
 * @date 2020/7/2 18:43
 */
public class DefaultTemplateFilePrefixNameStrategy implements TemplateFilePrefixNameStrategy {
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
        return Utils.getFileNameByPath(srcSource.substring(4),"\\_")+Utils.getFileNameByPath(template.getPath(),"\\/");
    }
}
