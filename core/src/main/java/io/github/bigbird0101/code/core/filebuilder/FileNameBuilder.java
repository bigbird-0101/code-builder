package io.github.bigbird0101.code.core.filebuilder;


import io.github.bigbird0101.code.core.template.Template;

/**
 * 文件名命名
 * @author fpp
 * @version 1.0
 * @date 2020/6/29 11:20
 */
public interface FileNameBuilder {
    /**
     * 文件名生成方法
     * @param template 模板
     * @return 最终的文件名
     */
    String nameBuilder(Template template);
}
