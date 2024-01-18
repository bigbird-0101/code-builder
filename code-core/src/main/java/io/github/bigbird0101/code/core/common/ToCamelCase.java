package io.github.bigbird0101.code.core.common;

import cn.hutool.core.util.StrUtil;

/**
 * 下划线转驼峰
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-12 14:57:29
 */
public class ToCamelCase implements TableNameToDomainName {
    @Override
    public String buildDomainName(String tableName) {
        return StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
    }
    @Override
    public String getType() {
        return DEFAULT;
    }
}
