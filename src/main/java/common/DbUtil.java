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
        }  else if (dataType == Types.REAL) {
            javaType = "Float";
        } else {
            javaType = getString(dataType);
        }
        return javaType;
    }

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
     * @param jdbcType jdbc数据类型
     * @param isUnsign 是否是无符号
     * @param maxLength 最大长度
     * @return
     */
    public static String getJavaTypeByJdbcType(String jdbcType,boolean isUnsign,int maxLength){
        if( jdbcType == null || jdbcType.trim().length() == 0 ) {
            return null;
        }
        jdbcType = jdbcType.toLowerCase();

        switch(jdbcType){
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
                if(isUnsign){
                    return "BigInteger";
                }
                return "Long";
            case "tinyint":
                return "Short";
            case "mediumint":
                return "Integer";
            case "smallint":
                if(isUnsign){
                    return "Integer";
                }
                return "Short";
            case "int":
                if(isUnsign){
                    return "Long";
                }
                return "Integer";
            case "bit":
                if(maxLength>1){
                    return "byte[]";
                }
                return "byte";
            case "boolean":
                return "Boolean";
            case "float":return "Float";
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
            case "time":return "Time";
            case "timestamp":return "Timestamp";
            default:
                throw new IllegalStateException("未找到与之匹配的类型"+jdbcType);
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
        }else if (dataType == Types.INTEGER) {
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
        return Arrays.stream(valueClassNameS).map(Utils::firstUpperCase).reduce((s, b) -> s + b).map(s->Utils.isEmpty(s)?Utils.firstUpperCase(tableName):s).get();
    }

    public static TableInfo getTableInfo(DataSourceConfig dataSourceConfigPojo, String tableName) throws SQLException, ClassNotFoundException {
        TableInfo tableInfo;
        Connection connection = DbUtil.getConnection(dataSourceConfigPojo);
        String dataBaseName = getDataBaseName(dataSourceConfigPojo.getUrl());
        PreparedStatement pStemt = connection.prepareStatement("Select *  from INFORMATION_SCHEMA.COLUMNS Where table_schema = '" + dataBaseName + "' and table_name ='" + tableName + "'");
        ResultSet rs = pStemt.executeQuery();
        List<TableInfo.ColumnInfo> columnInfoList = new ArrayList<>(16);
        while (rs.next()) {
            String name = rs.getString("COLUMN_NAME");
            String comment = rs.getString("COLUMN_COMMENT");
            boolean isNull = "yes".equalsIgnoreCase(rs.getString("IS_NULLABLE"));
            int size = rs.getInt("CHARACTER_MAXIMUM_LENGTH")==0?rs.getInt("NUMERIC_PRECISION"):rs.getInt("CHARACTER_MAXIMUM_LENGTH");
            boolean isUnsigned=rs.getString("COLUMN_TYPE").contains("unsigned");
            String javaType = getJavaTypeByJdbcType(rs.getString("DATA_TYPE"),isUnsigned,size);
            String domainPropertyName = getDomainPropertyName(name);
            String jdbcType =rs.getString("DATA_TYPE").toUpperCase();
            jdbcType=jdbcType.equals("INT")?"INTEGER":jdbcType;
            boolean isPrimaryKey = false;
            boolean isUniqueKey = false;
            String key=rs.getString("COLUMN_KEY");
            if(Utils.isNotEmpty(key)){
                isPrimaryKey= "PRI".equalsIgnoreCase(key);
                isUniqueKey= "UNI".equalsIgnoreCase(key);
            }
            TableInfo.ColumnInfo columnInfo = new TableInfo.ColumnInfo(name, comment, javaType, domainPropertyName, isNull, size, isPrimaryKey, isUniqueKey, jdbcType);
            columnInfoList.add(columnInfo);
        }
        if(columnInfoList.isEmpty()){
            throw new IllegalArgumentException("表"+tableName+"不存在");
        }
        PreparedStatement pStemtTables = connection.prepareStatement("Select table_name,TABLE_COMMENT  from INFORMATION_SCHEMA.TABLES Where table_schema = '" + dataBaseName + "' and table_name ='" + tableName + "'");
        rs = pStemtTables.executeQuery();
        String tableComment = "";
        while (rs.next()) {
            String tableNameTemp = rs.getString(1);
            String tableCommentTemp = rs.getString(2);
            if (tableName.equals(tableNameTemp)) {
                int index=tableCommentTemp.lastIndexOf("表");
                tableComment = index>0?tableCommentTemp.substring(0,index):tableCommentTemp;
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
        String url="jdbc:mysql://127.0.0.1:3306/xydj?useUnicode=true&characterEncoding=utf-8";
        String quDongName = url.indexOf("mysql") > 0 ? "com.mysql.jdbc.Driver" : url.indexOf("oracle") > 0 ? "" : "";
        DataSourceConfig a= new DataSourceConfig(quDongName,"root",url,"root");
        TableInfo tableInfo=getTableInfo(a,"tab_test");
        System.out.println(tableInfo);
    }
}
