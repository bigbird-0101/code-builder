### ServiceInterface 模版

```xml
<template>
<var name="methodBody" value="*{tool.sub(0,*{tableInfo.domainName}*)}*"/>
<var name="reqVoName" value="*{depend[0].className}*"/>
<var name="reqVoSimpleName" value="*{depend[0].simpleClassName}*"/>
<var name="firstLowerReqVoSimpleName" value="*{tool.firstLower(*{depend[0].simpleClassName}*)}*"/>
<var name="respVoName" value="*{depend[1].className}*"/>
<var name="respVoSimpleName" value="*{depend[1].simpleClassName}*"/>
<prefix>
package *{packageName}*;

import *{reqVoName}*;
import *{respVoName}*;
import *{respVoName}*;

import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.Set;

/**
 * *{tableInfo.tableComment}*业务处理接口类
 * @since  *{tool.currentDateTime()}*
 * @version 1.0.0
 * @author *{tool.author()}*
 */
public interface *{simpleClassName}* {
</prefix>
<function name="add">
    /**
     * 添加*{tableInfo.tableComment}*
     *
     * @param *{firstLowerReqVoSimpleName}* *{tableInfo.tableComment}*POJO
     * @return 返回是否成功
     */
    *{respVoSimpleName}* add*{methodBody}*(*{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*);
</function>
<function name="deleteArray">
    /**
     * 删除*{tableInfo.tableComment}* 根据其id数组
     *
     * @param ids *{tableInfo.tableComment}*id数组
     * @return 返回是否成功
     */
    boolean delete*{methodBody}*(Set&lt;Long&gt; ids<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>);
</function>
<function name="delete">
    /**
     * 删除*{tableInfo.tableComment}* 根据其id
     *
     * @param id *{tableInfo.tableComment}*id
     * @return 返回是否成功
     */
    default boolean delete*{methodBody}*(Long id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>){
        return delete*{methodBody}*(Collections.singleton(id)<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,userId</if></foreach>);
    }
</function>
<function name="edit">
    /**
     * 编辑*{tableInfo.tableComment}*
     *
     * @param *{firstLowerReqVoSimpleName}* *{tableInfo.tableComment}*POJO
     * @return 返回是否成功
     */
    *{respVoSimpleName}* edit*{methodBody}*(*{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*);
</function>
<function name="get">
    /**
     * 根据id获取*{tableInfo.tableComment}*信息列表
     *
     * @param id *{tableInfo.tableComment}*id
     * @return *{simpleClassName}*POJO
     */
    Optional&lt;*{respVoSimpleName}*&gt; get*{methodBody}*(Long id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>);
</function>
<function name="getList">
    /**
     * 根据id获取*{tableInfo.tableComment}*信息列表
     *
     * @param id *{tableInfo.tableComment}*id
     * @return *{simpleClassName}*POJO
     */
    List&lt;*{respVoSimpleName}*&gt; get*{methodBody}*List(Long id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>);
</function>
<function name="getPage">
    /**
     * 分页方法获取*{tableInfo.tableComment}*信息列表
     *
     * @param pageReqVo 分页请求参数
     * @return *{tableInfo.tableComment}*数据列表
     */
    &lt;T extends PageReqVo&gt; PageResult&lt;*{respVoSimpleName}*&gt; get*{methodBody}*Page(T pageReqVo);
</function>
<suffix>
}
</suffix>
</template>
```