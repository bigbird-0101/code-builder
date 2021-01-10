package com.fpp.code.core.template;

/**
 * 模板最终生成文件的文件前缀名命名策略
 * @author fpp
 * @version 1.0
 * @date 2020/7/2 18:16
 */
public interface TemplateFilePrefixNameStrategy {
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
    String prefixStrategy(Template template,String srcSource);

}
