package com.fpp.code.core.template.variable.resource;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.fpp.code.core.common.DbUtil;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.domain.DataSourceConfig;
import com.fpp.code.core.domain.TableInfo;
import com.fpp.code.core.exception.CodeConfigException;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库当中的模板资源
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-24 23:14:30
 */
public class DataSourceTemplateVariableResource implements TemplateVariableResource{
    private final DataSourceConfig dataSourceConfig;
    private final String tableName;
    private final Environment environment;

    private static final Map<String,TableInfo> TABLE_INFO_CACHE= new ConcurrentHashMap<>();

    public DataSourceTemplateVariableResource(DataSourceConfig dataSourceConfig, String tableName, Environment environment) {
        this.dataSourceConfig = dataSourceConfig;
        this.tableName = tableName;
        this.environment = environment;
    }

    public DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }

    public String getTableName() {
        return tableName;
    }

    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public Map<String, Object> getTemplateVariable() {
        if(!ObjectUtil.isAllNotEmpty(dataSourceConfig,tableName,environment)){
            throw new IllegalArgumentException(StrUtil.format(
                    "argument error dataSourceConfig {},tableName{},environment{}",dataSourceConfig,tableName,environment));
        }
        try {
            String cacheKey=StrUtil.format("{},{}",dataSourceConfig.toString(),tableName);
            TableInfo tableInfo = TABLE_INFO_CACHE.get(cacheKey);
            if(null!=tableInfo){
                return MapUtil.<String,Object>builder("tableInfo",tableInfo).map();
            }
            tableInfo = DbUtil.getTableInfo(dataSourceConfig, tableName,environment);
            TABLE_INFO_CACHE.put(cacheKey,tableInfo);
            return MapUtil.<String,Object>builder("tableInfo",tableInfo).map();
        } catch (SQLException e) {
            StaticLog.error("DataSourceTemplateVariableResource DbUtil.getTableInfo error",e);
            throw new CodeConfigException(e);
        }
    }
}
