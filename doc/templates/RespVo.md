### RespVo

```xml
<template>
package *{packageName}*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * *{tableInfo.tableComment}*RespVo
 * @since  *{tool.currentDateTime()}*
 * @version 1.0.0
 * @author *{tool.author()}*
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="*{tableInfo.tableComment}*对象模型")
public class *{simpleClassName}* implements Serializable{

    <foreach v-for="column in tableInfo.columnList">
    /**
      * *{column.comment}*
      */
    <choose>
    <when test="column.name=='id'">
    @ApiModelProperty(value="唯一id")
    private Long id;
    </when>
    <when test="column.name=='deleted'">
    @ApiModelProperty(value="是否被删除 0-否  1-是",hidden=true)
    @JsonIgnore
    private Integer deleted;
    </when>
    <when test="column.name=='created_time'">
    @ApiModelProperty(value="创建时间",hidden=true)
    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdTime;
    </when>
    <when test="column.name=='created_by'">
    @ApiModelProperty(value="创建者",hidden=true)
    @JsonIgnore
    private String createdBy;
    </when>
    <when test="column.name=='updated_time'">
    @ApiModelProperty(value="更新时间",hidden=true)
    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedTime;
    </when>
    <when test="column.name=='updated_by'">
    @ApiModelProperty(value="更新者",hidden=true)
    @JsonIgnore
    private String updatedBy;
    </when>
    <when test="column.name=='user_id'">
    @ApiModelProperty(value="用户id",hidden=true)
    @JsonIgnore
    private Long userId;
    </when>
    <otherwise>
    @ApiModelProperty(value="*{column.comment}*")
    private *{column.javaType}* *{column.domainPropertyName}*;
    </otherwise>
    </choose>
    </foreach>
}
</template>
```