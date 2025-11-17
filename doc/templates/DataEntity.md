### DataEntity 数据库对象模版

```xml
<template>
package *{packageName}*;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * *{tableInfo.tableComment}*对象模型
 * @since  *{tool.currentDateTime()}*
 * @version 1.0.0
 * @author *{tool.author()}*
 */
@Data
@TableName(value = "*{tableInfo.tableName}*")
public class *{simpleClassName}* extends BaseEntity{

    <foreach v-for="column in tableInfo.columnList">
    <choose>
    <when test="column.name=='id'">
    </when>
    <when test="column.name=='create_time'">
    </when>
    <when test="column.name=='create_by'">
    </when>
    <when test="column.name=='update_time'">
    </when>
    <when test="column.name=='update_by'">
    </when>
    <when test="column.name=='created_time'">
    </when>
    <when test="column.name=='created_by'">
    </when>
    <when test="column.name=='updated_time'">
    </when>
    <when test="column.name=='updated_by'">
    </when>
    <otherwise>
    /**
      * *{column.comment}*
      */
    @TableField(value = "*{column.name}*")
    private *{column.javaType}* *{column.domainPropertyName}*;
    </otherwise>
    </choose>
    </foreach>
}
</template>
```