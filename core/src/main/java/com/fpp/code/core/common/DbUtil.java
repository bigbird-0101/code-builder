package com.fpp.code.core.common;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LFUCache;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.TableType;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.domain.DataSourceConfig;
import com.fpp.code.core.domain.TableInfo;
import com.fpp.code.spi.NewInstanceServiceLoader;
import com.fpp.code.util.Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/2 18:54
 */
public class DbUtil {
    public static final LFUCache<String, Connection> CONNECTION_LFU_CACHE = CacheUtil.newLFUCache(2);
    static {
        NewInstanceServiceLoader.register(TableNameToDomainName.class);
        RuntimeUtil.addShutdownHook(() -> CONNECTION_LFU_CACHE.forEach(s -> {
            try {
                s.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }
    /**
     * 将数据库列类型转换为java数据类型
     *
     * @param dataType 列类型
     * @return String java数据类型
     */
    public static String getJavaType(int dataType) {
        String javaType = "";
        if (dataType == Types.INTEGER || dataType == Types.SMALLINT) {
            javaType = "Integer";
        } else if (dataType == Types.BIGINT) {
            javaType = "Long";
        } else if (dataType == Types.CHAR || dataType == Types.VARCHAR || dataType == Types.NVARCHAR
                || dataType == Types.CLOB || dataType == Types.BLOB) {
            javaType = "String";
        } else if (dataType == Types.TINYINT) {
            javaType = "Short";
        } else if (dataType == Types.FLOAT) {
            javaType = "Double";
        } else if (dataType == Types.REAL) {
            javaType = "Float";
        } else {
            javaType = getString(dataType);
        }
        return javaType;
    }

    /**
     *
     * @param dataType
     * @return
     */
    public static String getString(int dataType) {
        String javaType;
        if (dataType == Types.NUMERIC || dataType == Types.DECIMAL || dataType == Types.DOUBLE) {
            javaType = "BigDecimal";
        } else if (dataType == Types.DATE || dataType == Types.TIMESTAMP || dataType == Types.TIME) {
            javaType = "String";
        } else {
            javaType = "String";
        }
        return javaType;
    }

    /**
     * 通过jdbc type 获取java数据类型
     *
     * @param jdbcType  jdbc数据类型
     * @param isUnsign  是否是无符号
     * @param maxLength 最大长度
     * @return
     */
    public static String getJavaTypeByJdbcType(String jdbcType, boolean isUnsign, int maxLength) {
        if (jdbcType == null || jdbcType.trim().length() == 0) {
            return null;
        }
        jdbcType = jdbcType.toLowerCase();

        switch (jdbcType) {
            case "nvarchar":
            case "char":
            case "varchar":
            case "text":
            case "nchar":
                return "String";
            case "blob":
            case "image":
                return "byte[]";
            case "id":
            case "bigint":
                if (isUnsign) {
                    return "BigInteger";
                }
                return "Long";
            case "tinyint":
                return "Short";
            case "mediumint":
                return "Integer";
            case "smallint":
                if (isUnsign) {
                    return "Integer";
                }
                return "Short";
            case "integer":
            case "int":
                if (isUnsign) {
                    return "Long";
                }
                return "Integer";
            case "bit":
                if (maxLength > 1) {
                    return "byte[]";
                }
                return "byte";
            case "boolean":
                return "Boolean";
            case "float":
                return "Float";
            case "double":
            case "money":
            case "smallmoney":
            case "decimal":
            case "numeric":
            case "real":
                return "BigDecimal";
            case "date":
            case "datetime":
            case "year":
                return "Date";
            case "time":
                return "Time";
            case "timestamp":
                return "Timestamp";
            default:
                throw new IllegalStateException("未找到与之匹配的类型" + jdbcType);
        }
    }


    /**
     * 根据列的类型，获取mybatis配置中的jdbcType
     *
     * @param dataType 列的类型
     * @return String jdbcType
     */
    public static String getMybatisJdbcType(int dataType) {
        String jdbcType = "";
        if (dataType == Types.TINYINT) {
            jdbcType = "TINYINT";
        } else if (dataType == Types.SMALLINT) {
            jdbcType = "SMALLINT";
        } else if (dataType == Types.INTEGER) {
            jdbcType = "INTEGER";
        } else if (dataType == Types.BIGINT) {
            jdbcType = "BIGINT";
        } else if (dataType == Types.FLOAT) {
            jdbcType = "FLOAT";
        } else if (dataType == Types.DOUBLE) {
            jdbcType = "DOUBLE";
        } else if (dataType == Types.DECIMAL) {
            jdbcType = "DECIMAL";
        } else if (dataType == Types.NUMERIC) {
            jdbcType = "NUMERIC";
        } else if (dataType == Types.VARCHAR) {
            jdbcType = "VARCHAR";
        } else if (dataType == Types.NVARCHAR) {
            jdbcType = "NVARCHAR";
        } else if (dataType == Types.CHAR) {
            jdbcType = "CHAR";
        } else if (dataType == Types.NCHAR) {
            jdbcType = "NCHAR";
        } else if (dataType == Types.CLOB) {
            jdbcType = "CLOB";
        } else if (dataType == Types.BLOB) {
            jdbcType = "BLOB";
        } else if (dataType == Types.NCLOB) {
            jdbcType = "NCLOB";
        } else if (dataType == Types.DATE) {
            jdbcType = "DATE";
        } else if (dataType == Types.TIMESTAMP) {
            jdbcType = "TIMESTAMP";
        } else if (dataType == Types.ARRAY) {
            jdbcType = "ARRAY";
        } else if (dataType == Types.TIME) {
            jdbcType = "TIME";
        } else if (dataType == Types.BOOLEAN) {
            jdbcType = "BOOLEAN";
        } else if (dataType == Types.BIT) {
            jdbcType = "BIT";
        } else if (dataType == Types.BINARY) {
            jdbcType = "BINARY";
        } else if (dataType == Types.OTHER) {
            jdbcType = "OTHER";
        } else if (dataType == Types.REAL) {
            jdbcType = "REAL";
        } else if (dataType == Types.LONGVARCHAR) {
            jdbcType = "LONGVARCHAR";
        } else if (dataType == Types.VARBINARY) {
            jdbcType = "VARBINARY";
        } else if (dataType == Types.LONGVARBINARY) {
            jdbcType = "LONGVARBINARY";
        }

        return jdbcType;
    }


    /**
     * 获取数据库连接
     *
     * @param dataSourceConfigPojo
     * @return 获取数据库连接
     * @throws SQLException
     */
    private static Connection getConnection(DataSourceConfig dataSourceConfigPojo) throws SQLException {
        String quDongName = dataSourceConfigPojo.getQuDongName();
        String user = dataSourceConfigPojo.getUserName();
        String password = dataSourceConfigPojo.getPassword();
        String url = dataSourceConfigPojo.getUrl();
        Properties props = new Properties();
        props.put("remarksReporting", "true");
        props.put("user", user);
        props.put("password", password);
        final String cacheKey = StrUtil.format("{}{}", url, props.toString());
        Connection connection = CONNECTION_LFU_CACHE.get(cacheKey);
        if (null != connection) {
            return connection;
        }
        connection = DriverManager.getConnection(url, props);
        CONNECTION_LFU_CACHE.put(cacheKey, connection);
        return connection;
    }

    /**
     * 把以_分隔的列明转化为字段名,将大写的首字母变小写
     *
     * @param columnName 列名
     * @return String 字段名
     */
    private static String getDomainPropertyName(String columnName) {
        return Utils.getFieldName(Utils.underlineToHump(columnName));
    }

    private static String getDomainName(String tableName, Environment environment) {
        final String propertyOrDefault = environment.getPropertyOrDefault("code.table_name_to_domain_name", TableNameToDomainName.DEFAULT);
        final Properties properties = environment.getPropertySources().convertProperties();
        return new TableNameToDomainNameServiceLoader(TableNameToDomainName.class).newService(propertyOrDefault,properties).buildDomainName(tableName);
    }

    /**
     *
     * @param dataSourceConfigPojo
     * @param tableName
     * @param environment
     * @return 表详情
     * @throws SQLException
     */
    public static TableInfo getTableInfo(DataSourceConfig dataSourceConfigPojo, String tableName, Environment environment) throws SQLException {
        TableInfo tableInfo;
        Connection connection = DbUtil.getConnection(dataSourceConfigPojo);
        List<TableInfo.ColumnInfo> columnInfoList = new ArrayList<>(16);
        try(ResultSet rs = connection.getMetaData().getColumns(connection.getCatalog(), connection.getSchema(), tableName, null)) {
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                String comment = rs.getString("REMARKS");
                boolean isNull = rs.getBoolean("NULLABLE");
                int size = rs.getInt("COLUMN_SIZE");
                String javaType = getJavaType(rs.getInt("DATA_TYPE"));
                String domainPropertyName = getDomainPropertyName(name);
                String jdbcType = getMybatisJdbcType(rs.getInt("DATA_TYPE"));
                jdbcType = "INT".equals(jdbcType) ? "INTEGER" : jdbcType;
                jdbcType = "DATETIME".equals(jdbcType) ? "TIMESTAMP" : jdbcType;
                boolean isPrimaryKey = false;
                boolean isUniqueKey = false;
                // 获得主键
                try (ResultSet rs2 = connection.getMetaData().getPrimaryKeys(connection.getCatalog(), connection.getSchema(), tableName)) {
                    if (null != rs2) {
                        while (rs2.next()) {
                            final String primaryKeysName = rs.getString("COLUMN_NAME");
                            if (name.equals(primaryKeysName)) {
                                isPrimaryKey = true;
                            }
                        }
                    }
                }
                TableInfo.ColumnInfo columnInfo = new TableInfo.ColumnInfo(name, comment, javaType, domainPropertyName, isNull, size, isPrimaryKey, isUniqueKey, jdbcType);
                columnInfoList.add(columnInfo);
            }
            if (columnInfoList.isEmpty()) {
                throw new IllegalArgumentException("表" + tableName + "不存在");
            }
        }
        String tableComment = "";
        try (ResultSet rs = connection.getMetaData().getTables(connection.getCatalog(), connection.getSchema(), tableName, new String[]{TableType.TABLE.value()})) {
            if (null != rs) {
                if (rs.next()) {
                    String tableCommentTemp=rs.getString("REMARKS");
                    tableComment = StrUtil.removeSuffix(tableCommentTemp,"表");
                }
            }
        }
        tableInfo = new TableInfo(tableName, tableComment, getDomainName(tableName,environment), columnInfoList);
        return tableInfo;
    }


    /**
     * 通过数据库来得到所有表名
     */
    public static List<String> getAllTableName(DataSourceConfig dataSourceConfigPojo) {
        List<String> tableNameS;
        Connection connection = null;
        try {
            connection = DbUtil.getConnection(dataSourceConfigPojo);
            tableNameS = new ArrayList<>();
            DatabaseMetaData dbmd = connection.getMetaData();

            ResultSet rs = dbmd.getTables(null, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                // 获得表名
                String tableName = rs.getString("TABLE_NAME");
                tableNameS.add(tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tableNameS;
    }
}