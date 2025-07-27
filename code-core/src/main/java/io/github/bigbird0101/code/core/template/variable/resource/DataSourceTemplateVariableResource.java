package io.github.bigbird0101.code.core.template.variable.resource;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import io.github.bigbird0101.code.core.cache.Cache;
import io.github.bigbird0101.code.core.cache.CachePool;
import io.github.bigbird0101.code.core.common.DbUtil;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.domain.DataSourceConfig;
import io.github.bigbird0101.code.core.domain.TableInfo;
import io.github.bigbird0101.code.core.exception.CodeConfigException;

import java.sql.SQLException;
import java.util.Map;

/**
 * 数据库当中的模板资源
 *
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-24 23:14:30
 */
public record DataSourceTemplateVariableResource(String tableName,
                                                 Environment environment) implements TemplateVariableResource {
    private static final Cache<String, TableInfo> TABLE_INFO_CACHE = CachePool.build(10);

    @Override
    public Map<String, Object> getTemplateVariable() {
        if (!ObjectUtil.isAllNotEmpty(environment, tableName)) {
            throw new IllegalArgumentException(StrUtil.format(
                    "argument error tableName{},environment", environment, tableName));
        }
        try {
            final DataSourceConfig dataSourceConfig = DataSourceConfig.getDataSourceConfig(environment);
            String cacheKey = StrUtil.format("{},{}", dataSourceConfig.toString(), tableName);
            TableInfo tableInfo = TABLE_INFO_CACHE.get(cacheKey);
            if (null != tableInfo) {
                return MapUtil.<String, Object>builder("tableInfo", tableInfo).map();
            }
            tableInfo = DbUtil.getTableInfo(dataSourceConfig, tableName, environment);
            TABLE_INFO_CACHE.put(cacheKey, tableInfo);
            return MapUtil.<String, Object>builder("tableInfo", tableInfo).map();
        } catch (SQLException e) {
            StaticLog.error("DataSourceTemplateVariableResource DbUtil.getTableInfo error", e);
            throw new CodeConfigException(e);
        }
    }

    @Override
    public String getType() {
        return "datasource";
    }

}
