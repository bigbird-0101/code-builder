### Controller 模版

```xml

<template>
    <var name="doName" value="*{depend[0].className}*"/>
    <var name="doSimpleName" value="*{depend[0].simpleClassName}*"/>
    <var name="firstLowerDoSimpleName" value="*{tool.firstLower(*{doSimpleName}*)}*"/>
    <var name="reqVoName" value="*{depend[1].className}*"/>
    <var name="reqVoSimpleName" value="*{depend[1].simpleClassName}*"/>
    <var name="firstLowerReqVoSimpleName" value="*{tool.firstLower(*{reqVoSimpleName}*)}*"/>
    <var name="respVoName" value="*{depend[2].className}*"/>
    <var name="respVoSimpleName" value="*{depend[2].simpleClassName}*"/>
    <var name="firstLowerRespVoSimpleName" value="*{tool.firstLower(*{respVoSimpleName}*)}*"/>
    <prefix>
        package *{packageName}*;

        import *{doName}*;
        import *{reqVoName}*;
        import *{respVoName}*;
        import io.swagger.annotations.Api;
        import io.swagger.annotations.ApiImplicitParam;
        import io.swagger.annotations.ApiImplicitParams;
        import io.swagger.annotations.ApiOperation;
        import lombok.RequiredArgsConstructor;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.validation.annotation.Validated;
        import org.springframework.web.bind.annotation.*;
        import javax.validation.Valid;
        import java.util.List;
        import java.util.Set;
        <foreach v-for="column in tableInfo.columnList">
            <if v-if="column.name=='user_id'">
            </if>
        </foreach>
        /**
        * *{tableInfo.tableComment}*类
        * @since *{tool.currentDateTime()}*
        * @version 1.0.0
        * @author *{tool.author()}*
        */
        @RestController
        @RequestMapping("/*{tool.rep(*{tableInfo.tableName}*,'_','-')}*")
        @Api(value = "*{tableInfo.tableComment}*")
        @Validated
        @RequiredArgsConstructor
        public class *{tableInfo.domainName}*Controller {

        /**
        * 业务处理类注入
        */
        private final *{doSimpleName}* *{firstLowerDoSimpleName}*;
    </prefix>
    <function name="add">
        /**
        * 添加*{tableInfo.tableComment}*
        *
        * @param *{firstLowerReqVoSimpleName}* *{tableInfo.tableComment}*信息
        * @return 数据处理结果
        */
        @PostMapping("/add")
        @ApiOperation(value = "添加*{tableInfo.tableComment}*", hidden=true)
        public Result&lt;*{respVoSimpleName}*&gt; add*{tableInfo.domainName}*(@Valid @RequestBody *{reqVoSimpleName}*
        *{firstLowerReqVoSimpleName}*) {
        <foreach v-for="column in tableInfo.columnList">
            <if v-if="column.name=='user_id'">
                *{firstLowerReqVoSimpleName}*.setUserId(UserUtils.getUserId());
            </if>
        </foreach>
        return Result.success(*{firstLowerDoSimpleName}*.add*{tableInfo.domainName}*(*{firstLowerReqVoSimpleName}*));
        }
    </function>
    <function name="delete">
        /**
        * 删除*{tableInfo.tableComment}*根据id数组
        *
        * @param ids *{tableInfo.tableComment}*id数组
        * @return 数据处理结果
        */
        @DeleteMapping("/delete")
        @ApiOperation(value = "删除*{tableInfo.tableComment}*", hidden=true)
        @ApiImplicitParam(paramType = "query", name = "idS", value = "*{tableInfo.tableComment}*id数组", required =
        true, dataType = "String")
        public Result&lt;Boolean&gt; delete*{tableInfo.domainName}*(@RequestParam Set&lt;Long&gt; ids) {
        return Result.success(*{firstLowerDoSimpleName}*.delete*{tableInfo.domainName}*(ids
        <foreach v-for="column in tableInfo.columnList">
            <if v-if="column.name=='user_id'">,UserUtils.getUserId()</if>
        </foreach>
        ));
        }
    </function>
    <function name="edit">
        /**
        * 修改*{tableInfo.tableComment}*
        *
        * @param *{firstLowerReqVoSimpleName}* *{tableInfo.tableComment}*信息
        * @return 数据处理结果
        */
        @PutMapping("/edit")
        @ApiOperation(value = "修改*{tableInfo.tableComment}*", hidden=true)
        public Result&lt;*{respVoSimpleName}*&gt; edit*{tableInfo.domainName}*(@Valid @RequestBody *{reqVoSimpleName}*
        *{firstLowerReqVoSimpleName}*) {
        <foreach v-for="column in tableInfo.columnList">
            <if v-if="column.name=='user_id'">
                *{firstLowerReqVoSimpleName}*.setUserId(UserUtils.getUserId());
            </if>
        </foreach>
        return Result.success(*{firstLowerDoSimpleName}*.edit*{tableInfo.domainName}*(*{firstLowerReqVoSimpleName}*));
        }
    </function>
    <function name="get">
        /**
        * 根据id 获取*{tableInfo.tableComment}*
        *
        * @param id *{tableInfo.tableComment}*的id
        * @return 数据处理结果
        */
        @GetMapping("/get")
        @ApiOperation(value = "根据id获取*{tableInfo.tableComment}*", notes = "返回*{tableInfo.tableComment}*详细信息",
        hidden=true)
        @ApiImplicitParam(paramType = "query", name = "id", value = "*{tableInfo.tableComment}*id", required = true,
        dataType = "Long")
        public Result&lt;*{respVoSimpleName}*&gt; get*{tableInfo.domainName}*(@RequestParam Long id) {
        return Result.success(*{firstLowerDoSimpleName}*.get*{tableInfo.domainName}*(id
        <foreach v-for="column in tableInfo.columnList">
            <if v-if="column.name=='user_id'">,UserUtils.getUserId()</if>
        </foreach>
        ).orElse(null));
        }
    </function>
    <function name="getAll">
        /**
        * 分页方法
        * 获取*{tableInfo.tableComment}*列表信息
        *
        * @param pageReqVo 分页参数
        * @return 数据处理结果
        */
        @GetMapping("/getAllList")
        @ApiOperation(value = "获取*{tableInfo.tableComment}*列表", notes = "返回*{tableInfo.tableComment}*列表信息",
        hidden=true)
        public Result&lt;PageResult&lt;*{respVoSimpleName}*&gt;&gt; getAll*{tableInfo.domainName}*Page(@Valid PageReqVo
        pageReqVo){
        <foreach v-for="column in tableInfo.columnList">
            <if v-if="column.name=='user_id'">
                pageReqVo=copy(pageReqVo,UserUtils.getUserId());
            </if>
        </foreach>
        return Result.success(*{firstLowerDoSimpleName}*.get*{tableInfo.domainName}*Page(pageReqVo));
        }
    </function>
    <suffix>
        }
    </suffix>
</template>
```