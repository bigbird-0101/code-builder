package main.java.common;

import main.java.domain.DataSourceConfig;
import main.java.template.TableInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/2 18:54
 */
public class DbUtil {
    /**
     * 将数据库列类型转换为java数据类型
     *
     * @param dataType 列类型
     * @return String java数据类型
     */
    private static String getJavaType(int dataType) {
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
        }  else if (dataType == Types.REAL) {
            javaType = "Float";
        } else if (dataType == Types.NUMERIC || dataType == Types.DECIMAL || dataType == Types.DOUBLE) {
            javaType = "BigDecimal";
        } else if (dataType == Types.DATE || dataType == Types.TIMESTAMP || dataType == Types.TIME) {
            javaType = "String";
        } else {
            javaType = "String";
        }
        return javaType;
    }


    /**
     * 根据列的类型，获取mybatis配置中的jdbcType
     *
     * @param dataType 列的类型
     * @return String jdbcType
     */
    private static String getMybatisJdbcType(int dataType) {
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
     * 根据连接获取数据库名
     */
    public static String getDataBaseName(String url) {
        String beforeValue = url.substring(0, url.indexOf("?"));
        beforeValue = beforeValue.replace("//", "*");
        return beforeValue.substring(beforeValue.indexOf("/") + 1);
    }

    /**
     * 获取数据库连接
     *
     * @param dataSourceConfigPojo
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static Connection getConnection(DataSourceConfig dataSourceConfigPojo) throws ClassNotFoundException, SQLException {
        String quDongName = dataSourceConfigPojo.getQuDongName();
        String user = dataSourceConfigPojo.getUserName();
        String password = dataSourceConfigPojo.getPassword();
        String url = dataSourceConfigPojo.getUrl();
        Properties props = new Properties();
        props.put("remarksReporting", "true");
        props.put("user", user);
        props.put("password", password);
        Class.forName(quDongName);
        return DriverManager.getConnection(url, props);
    }

    /**
     * 把以_分隔的列明转化为字段名,将大写的首字母变小写
     *
     * @param columnName 列名
     * @return String 字段名
     */
    private static String getDomainPropertyName(String columnName) {
        if (columnName == null) {
            return "";
        }
        StringBuilder fieldNameBuffer = new StringBuilder();

        boolean nextUpperCase = false;
        for (int i = 0; i < columnName.length(); i++) {
            char c = columnName.charAt(i);

            if (nextUpperCase) {
                fieldNameBuffer.append(columnName.substring(i, i + 1).toUpperCase());
            } else {
                fieldNameBuffer.append(c);
            }

            if (c == '_') {
                nextUpperCase = true;
            } else {
                nextUpperCase = false;
            }
        }

        String fieldName = fieldNameBuffer.toString();
        fieldName = fieldName.replaceAll("_", "");

        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
        return fieldName;
    }

    private static String getDomainName(String tableName) {
        String[] valueClassNameS = tableName.substring(4).split("_");
        return Arrays.stream(valueClassNameS).map(Utils::firstUpperCase).reduce((s, b) -> s + b).get();
    }

    public static TableInfo getTableInfo(DataSourceConfig dataSourceConfigPojo, String tableName) throws SQLException, ClassNotFoundException {
        TableInfo tableInfo = null;
        Connection connection = DbUtil.getConnection(dataSourceConfigPojo);
        DatabaseMetaData dbmd = connection.getMetaData();
        ResultSet rs = dbmd.getColumns(null, null, tableName, null);
        List<TableInfo.ColumnInfo> columnInfoList = new ArrayList<>(20);
        while (rs.next()) {
            String name = rs.getString("COLUMN_NAME");
            String javaType = getJavaType(rs.getInt("DATA_TYPE"));
            String comment = rs.getString("REMARKS");
            boolean isNull = rs.getInt("NULLABLE") == 1;
            int size = rs.getInt("COLUMN_SIZE");
            String domainPropertyName = getDomainPropertyName(name);
            String jdbcType = getMybatisJdbcType(rs.getInt("DATA_TYPE"));
            boolean isPrimaryKey = false;
            boolean isUniqueKey = false;
            ResultSet rs1 = dbmd.getPrimaryKeys(null, null, tableName);
            while (rs1.next()) {
                if (rs.getString("COLUMN_NAME").equals(name)) {
                    isPrimaryKey = true;
                }
            }
            //mysql数据库
            ResultSet indexInfo = dbmd.getIndexInfo(null, null, tableName, false, false);
            while (indexInfo.next()) {
                String indexName = indexInfo.getString("INDEX_NAME");
                //如果为真则说明索引值不唯一，为假则说明索引值必须唯一。
                boolean nonUnique = indexInfo.getBoolean("NON_UNIQUE");
                if (!nonUnique && indexName.equals(name)) {
                    isUniqueKey = true;
                }
            }
            TableInfo.ColumnInfo columnInfo = new TableInfo.ColumnInfo(name, comment, javaType, domainPropertyName, isNull, size, isPrimaryKey, isUniqueKey, jdbcType);
            columnInfoList.add(columnInfo);
        }
        if(columnInfoList.isEmpty()){
            throw new IllegalArgumentException("表"+tableName+"不存在");
        }
        String dataBaseName = getDataBaseName(dataSourceConfigPojo.getUrl());
        PreparedStatement pStemt = connection.prepareStatement("Select table_name,TABLE_COMMENT  from INFORMATION_SCHEMA.TABLES Where table_schema = '" + dataBaseName + "' and table_name ='" + tableName + "'");
        rs = pStemt.executeQuery();
        String tableComment = "";
        while (rs.next()) {
            String tableNameTemp = rs.getString(1);
            String tableCommentTemp = rs.getString(2);
            if (tableName.equals(tableNameTemp)) {
                tableComment = tableCommentTemp;
            }
        }
        tableInfo = new TableInfo(tableName, tableComment, getDomainName(tableName), columnInfoList);
        return tableInfo;
    }


    /**
     * 通过数据库来得到所有表名
     */
    public static List<String> getAllTableName(DataSourceConfig dataSourceConfigPojo) throws SQLException, ClassNotFoundException {
        List<String> tableNameS;
        Connection connection = DbUtil.getConnection(dataSourceConfigPojo);
        try {
            tableNameS = new ArrayList<String>();
            DatabaseMetaData dbmd = connection.getMetaData();

            ResultSet rs = dbmd.getTables(null, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                // 获得表名
                String tableName = rs.getString("TABLE_NAME");
                tableNameS.add(tableName);
            }
        } finally {
            try {
                assert connection != null;
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tableNameS;
    }


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String url="jdbc:mysql://192.168.1.110:3306/car_wechat_xcx?useUnicode=true&characterEncoding=utf-8";
        String quDongName = url.indexOf("mysql") > 0 ? "com.mysql.jdbc.Driver" : url.indexOf("oracle") > 0 ? "" : "";
        DataSourceConfig a= new DataSourceConfig(quDongName,"root",url,"pttdata");
        TableInfo tableInfo=getTableInfo(a,"tab_order");
        System.out.println(tableInfo);
    }
}
