### dom不可控制方法模版(DomNoHandleFunctionTemplate)

```xml
<template>
package *{packageName}*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * *{tableInfo.tableComment}*Dto
 * @since  *{tool.currentDateTime()}*
 * @version 1.0.0
 * @author *{tool.author()}*
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class *{tableInfo.domainName}*Dto implements Serializable{

    <foreach v-for="column in tableInfo.columnList">
   /**
      * *{column.comment}*
      */
    <choose>
    <when test="column.name=='id'">
    private Long id;
    </when>
    <when test="column.name=='created_time'">
    private Date createdTime;
    </when>
    <when test="column.name=='create_time'">
    private Date createTime;
    </when>
    <when test="column.name=='create_by'">
    private String createBy;
    </when>
    <when test="column.name=='updated_time'">
    private Date updatedTime;
    </when>
    <when test="column.name=='update_time'">
    private Date updateTime;
    </when>
    <when test="column.name=='update_by'">
    private String updateBy;
    </when>
    <otherwise>
    private *{column.javaType}* *{column.domainPropertyName}*;
    </otherwise>
    </choose>
    </foreach>
}
</template>
```