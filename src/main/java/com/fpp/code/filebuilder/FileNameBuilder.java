package com.fpp.code.filebuilder;

import com.fpp.code.template.Template;

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
     * @param tableName 表名
     * @return 最终的文件名
     */
    String nameBuilder(Template template,String tableName);
}
