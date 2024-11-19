package io.github.bigbird0101.code.core;

import io.github.bigbird0101.code.core.domain.TableInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataModelProvider {
    public static Map<String, Object> getDataModel() {
        TableInfo tableInfo = getTableInfo();
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("tableInfo", tableInfo);
        return dataModel;
    }

    public static TableInfo getTableInfo() {
        // 创建 ColumnInfo 对象
        TableInfo.ColumnInfo idColumn = new TableInfo.ColumnInfo(
                "id", "主键ID", "Long", "id", false, 11, true, false, "BIGINT"
        );

        TableInfo.ColumnInfo nameColumn = new TableInfo.ColumnInfo(
                "name", "名称", "String", "name", true, 255, false, false, "VARCHAR"
        );

        TableInfo.ColumnInfo ageColumn = new TableInfo.ColumnInfo(
                "age", "年龄", "Integer", "age", true, 3, false, false, "INT"
        );

        TableInfo.ColumnInfo emailColumn = new TableInfo.ColumnInfo(
                "email", "电子邮件", "String", "email", true, 255, false, false, "VARCHAR"
        );

        TableInfo.ColumnInfo titleColumn = new TableInfo.ColumnInfo(
                "title", "标题", "String", "title", true, 255, false, false, "VARCHAR"
        );

        // 创建 TableInfo 对象
        return new TableInfo(
                "users", "用户表", "User", Arrays.asList(idColumn, nameColumn,
                ageColumn, emailColumn, titleColumn)
        );
    }
}
