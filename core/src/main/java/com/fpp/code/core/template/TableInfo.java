package com.fpp.code.core.template;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/2 17:41
 */
public class TableInfo implements Serializable {
    private String tableName;
    private String tableComment;
    private String domainName;
    private String savePath;
    private List<ColumnInfo> columnList;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public List<ColumnInfo> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<ColumnInfo> columnList) {
        this.columnList = columnList;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public TableInfo(String tableName, String tableComment, String domainName, List<ColumnInfo> columnList) {
        this.tableName = tableName;
        this.tableComment = tableComment;
        this.domainName = domainName;
        this.columnList = columnList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableInfo tableInfo = (TableInfo) o;
        return Objects.equals(tableName, tableInfo.tableName) &&
                Objects.equals(tableComment, tableInfo.tableComment) &&
                Objects.equals(domainName, tableInfo.domainName) &&
                Objects.equals(savePath, tableInfo.savePath) &&
                Objects.equals(columnList, tableInfo.columnList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, tableComment, domainName, savePath, columnList);
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "tableName='" + tableName + '\'' +
                ", tableComment='" + tableComment + '\'' +
                ", domainName='" + domainName + '\'' +
                ", columnList=" + columnList +
                '}';
    }

    public static class ColumnInfo implements Serializable{
        private String name;
        private String comment;
        private String javaType;
        private String domainPropertyName;
        private boolean isNull;
        private int size;
        private boolean isPrimaryKey;
        private boolean isUniqueKey;
        private String jdbcType;

        public String getJdbcType() {
            return jdbcType;
        }

        public void setJdbcType(String jdbcType) {
            this.jdbcType = jdbcType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getJavaType() {
            return javaType;
        }

        public void setJavaType(String javaType) {
            this.javaType = javaType;
        }

        public boolean isNull() {
            return isNull;
        }

        public void setNull(boolean aNull) {
            isNull = aNull;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public boolean isPrimaryKey() {
            return isPrimaryKey;
        }

        public void setPrimaryKey(boolean primaryKey) {
            isPrimaryKey = primaryKey;
        }

        public boolean isUniqueKey() {
            return isUniqueKey;
        }

        public void setUniqueKey(boolean uniqueKey) {
            isUniqueKey = uniqueKey;
        }

        public String getDomainPropertyName() {
            return domainPropertyName;
        }

        public void setDomainPropertyName(String domainPropertyName) {
            this.domainPropertyName = domainPropertyName;
        }

        public ColumnInfo(String name, String comment, String javaType, String domainPropertyName, boolean isNull, int size, boolean isPrimaryKey, boolean isUniqueKey, String jdbcType) {
            this.name = name;
            this.comment = comment;
            this.javaType = javaType;
            this.domainPropertyName = domainPropertyName;
            this.isNull = isNull;
            this.size = size;
            this.isPrimaryKey = isPrimaryKey;
            this.isUniqueKey = isUniqueKey;
            this.jdbcType = jdbcType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ColumnInfo that = (ColumnInfo) o;
            return isNull == that.isNull &&
                    size == that.size &&
                    isPrimaryKey == that.isPrimaryKey &&
                    isUniqueKey == that.isUniqueKey &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(comment, that.comment) &&
                    Objects.equals(javaType, that.javaType) &&
                    Objects.equals(domainPropertyName, that.domainPropertyName) &&
                    Objects.equals(jdbcType, that.jdbcType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, comment, javaType, domainPropertyName, isNull, size, isPrimaryKey, isUniqueKey, jdbcType);
        }

        @Override
        public String toString() {
            return "ColumnInfo{" +
                    "name='" + name + '\'' +
                    ", comment='" + comment + '\'' +
                    ", javaType='" + javaType + '\'' +
                    ", domainPropertyName='" + domainPropertyName + '\'' +
                    ", isNull=" + isNull +
                    ", size=" + size +
                    ", isPrimaryKey=" + isPrimaryKey +
                    ", isUniqueKey=" + isUniqueKey +
                    ", jdbcType='" + jdbcType + '\'' +
                    '}';
        }
    }
}
