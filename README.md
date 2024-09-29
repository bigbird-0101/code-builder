### 前言

代码生成器.

### 亮点

1.根据数据表格或自定义变量,和自定义的模板，生成任意你想生成的代码，避免项目中重复写相似的，大大提高开发效率。

2.动态操作模板中的每一个方法，生成你想生成目标文件的任意方法。

3.以已有模板中的方法为基础，根据自定义参数，生成类似的代码。

### QUICK START

###### 1.通过代码直接启动 HelloWorld

1. 创建一个文件,文件名为 hello_world.template

文件内容为

```txt
I'm *{helloWorld}*
```

2. 引入core 依赖

   ```maven
   <dependency>
       <groupId>io.github.bigbird-0101</groupId>
       <artifactId>code-core</artifactId>
       <version>3.0.4</version>
   </dependency>
   ```
   
   ```java
   package com.fpp.code.core.filebuilder.definedfunction;
   
   import io.github.bigbird0101.code.core.config.StandardEnvironment;
   import io.github.bigbird0101.code.core.context.GenericTemplateContext;
   import io.github.bigbird0101.code.core.factory.TemplateDefinitionBuilder;
   import io.github.bigbird0101.code.core.template.DefaultNoHandleFunctionTemplate;
   import io.github.bigbird0101.code.core.template.Template;
   import org.junit.jupiter.api.Assertions;
   import org.junit.jupiter.api.Test;
   
   import java.util.HashMap;
   import java.util.Map;
   
   public class HelloWorldTest {
       @Test
       public void helloWorld() {
           StandardEnvironment environment=new StandardEnvironment();
           GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
           final Template dao = genericTemplateContext.getTemplate("hello_world");
           Map<String, Object> temp = new HashMap<>(10);
           temp.put("helloWorld","hello world");
           dao.getTemplateVariables().putAll(temp);
           final String templateResult = dao.getTemplateResult();
           Assertions.assertNotNull(templateResult);
       }
   }
   
   ```
   


###### 2.下载 [codebuilder压缩包](https://github.com/bigbird-0101/code-builder/releases) 

解压点击启动**codebuilder.exe**

![image-20220914225842169](https://s3.bmp.ovh/imgs/2022/09/14/22aa2bfa51c1845e.png)

**可修改data中的模板(模板必须为.template 后缀名) 达到修改生成的内容**

项目启动后
![image-20220914222145472](https://s3.bmp.ovh/imgs/2022/09/14/153192b899a698bf.png)

### **参数详解**

1.`code.properties 配置文件详解`

   default为数据源的配置名 可通过页面配置

1. `code.datasource.default.url`  数据源地址
2. `code.datasource.default.username` 数据源用户名
3. `code.datasource.default.password` 数据源密码
4. `code.datasource.names=["default2","default3","default","mysql"]` 为数据源的集合
5. `code.datasource=mysql` 为选中的数据源名
6. `code.project.file.project-author`  项目的作者

`2.templates.json 配置文件详解`

配置的模板  为json数组

1. `fileName`  模板文件名(必须与data/template文件名一致)
2. `name` 模板的名字
3. `isHandleFunction`  是否是能够控制模板中的方法的模板 1-是 0-否
4. `filePrefixNameStrategy` 文件的前缀命令策略   （默认）1-从表格名第四个字符开始+源码路径最后一个路径首字母大写  2-从表格名的第四个字符开始 3-根据前缀名表达式生成前缀名
5. `filePrefixNameStrategyPattern`  当filePrefixNameStrategy=3 时 这个为最终生成文件的前缀名表达式  `*{tableInfo.domainName}*RpcImplService` 当 tableInfo.domainName ==你好时  此时文件前缀名为: `你好RpcImplService`
6. `fileSuffixName` 文件的后缀名  默认 java
7. `ProjectUrl` 项目路径
8. `Module` 模块路径
9. `sourcesRoot` 源码根路径
10. `srcPackage` 最终源码包路径

### 模板详解

#### dom可控制方法模版(DomHandleFunctionTemplate) 推荐使用

##### 模版定义

```xml
<template>
    <prefix>
       模板前缀
    </prefix>
    <function name="abcd">
        abcd
    </function>
    <suffix>
        模板后缀
    </suffix>
</template>
```

##### 例子:

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
import cn.com.infinova.govern.common.model.vo.PageReqVo;
import cn.com.infinova.govern.common.model.utils.PageResult;
import cn.com.infinova.govern.common.model.utils.Result;
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
import static cn.com.infinova.govern.common.model.vo.UserPageReqVo.copy;
import cn.com.infinova.govern.common.model.utils.UserUtils;
</if>
</foreach>
/**
 * *{tableInfo.tableComment}*类
 * @since  *{tool.currentDateTime()}*
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
     * @param *{firstLowerReqVoSimpleName}*       *{tableInfo.tableComment}*信息
     * @return 数据处理结果
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加*{tableInfo.tableComment}*", hidden=true)
    public Result&lt;*{respVoSimpleName}*&gt; add*{tableInfo.domainName}*(@Valid @RequestBody *{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*) {
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
    @ApiImplicitParam(paramType = "query", name = "idS", value = "*{tableInfo.tableComment}*id数组", required = true, dataType = "String")
    public Result&lt;Boolean&gt; delete*{tableInfo.domainName}*(@RequestParam Set&lt;Long&gt; ids) {
        return Result.success(*{firstLowerDoSimpleName}*.delete*{tableInfo.domainName}*(ids<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,UserUtils.getUserId()</if></foreach>));
    }
    </function>
    <function name="edit">
    /**
     * 修改*{tableInfo.tableComment}*
     *
     * @param *{firstLowerReqVoSimpleName}*       *{tableInfo.tableComment}*信息
     * @return 数据处理结果
     */
    @PutMapping("/edit")
    @ApiOperation(value = "修改*{tableInfo.tableComment}*", hidden=true)
    public Result&lt;*{respVoSimpleName}*&gt; edit*{tableInfo.domainName}*(@Valid @RequestBody *{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*) {
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
    @ApiOperation(value = "根据id获取*{tableInfo.tableComment}*", notes = "返回*{tableInfo.tableComment}*详细信息", hidden=true)
    @ApiImplicitParam(paramType = "query", name = "id", value = "*{tableInfo.tableComment}*id", required = true, dataType = "Long")
    public Result&lt;*{respVoSimpleName}*&gt; get*{tableInfo.domainName}*(@RequestParam Long id) {
        return Result.success(*{firstLowerDoSimpleName}*.get*{tableInfo.domainName}*(id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,UserUtils.getUserId()</if></foreach>).orElse(null));
    }
    </function>
    <function name="getAll">
    /**
     * 分页方法
     * 获取*{tableInfo.tableComment}*列表信息
     *
     * @param pageReqVo  分页参数
     * @return 数据处理结果
     */
    @GetMapping("/getAllList")
    @ApiOperation(value = "获取*{tableInfo.tableComment}*列表", notes = "返回*{tableInfo.tableComment}*列表信息", hidden=true)
    public Result&lt;PageResult&lt;*{respVoSimpleName}*&gt;&gt; getAll*{tableInfo.domainName}*Page(@Valid PageReqVo pageReqVo){
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

#### dom不可控制方法模版(DomNoHandleFunctionTemplate) 推荐使用

##### 模版定义

```xml
<template>
    模版内容
</template>
```

##### 例子：

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

#### dom可控制方法有依赖模版（HaveDependTemplateDomHandleFunctionTemplate）推荐使用

这个模版定义与 DomHandleFunctionTemplate 相同 与 他的却别就是 可以在模版文件当中使用

\*{depend[1].simpleClassName}\* 这个语句 代表的意思是 第二个模版的简单类型，他这个索引是从0开始

##### 例子

```xml
<template>
<var name="doName" value="*{depend[0].className}*"/>
<var name="doSimpleName" value="*{depend[0].simpleClassName}*"/>
<var name="firstLowerDoSimpleName" value="*{tool.firstLower(*{doSimpleName}*)}*"/>
<var name="daoName" value="*{depend[1].className}*"/>
<var name="daoSimpleName" value="*{depend[1].simpleClassName}*"/>
<var name="firstLowerDaoSimpleName" value="*{tool.firstLower(*{daoSimpleName}*)}*"/>
<var name="reqVoName" value="*{depend[2].className}*"/>
<var name="reqVoSimpleName" value="*{depend[2].simpleClassName}*"/>
<var name="firstLowerReqVoSimpleName" value="*{tool.firstLower(*{reqVoSimpleName}*)}*"/>
<var name="serviceImplName" value="*{depend[3].className}*"/>
<var name="serviceImplSimpleName" value="*{depend[3].simpleClassName}*"/>
<var name="respVoName" value="*{depend[4].className}*"/>
<var name="respVoSimpleName" value="*{depend[4].simpleClassName}*"/>
<prefix>
package *{packageName}*;

import *{doName}*;
import *{daoName}*;
import *{reqVoName}*;
import *{serviceImplName}*;
import *{respVoName}*;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import cn.com.infinova.govern.common.model.utils.PageResult;
import cn.com.infinova.govern.common.model.vo.PageReqVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import static cn.com.infinova.govern.common.model.enums.Status.INTERNAL_SERVER_ERROR;
import static cn.com.infinova.govern.common.model.enums.Status.REQUEST_DATA_QUERY_ERROR;
import static cn.com.infinova.govern.common.model.utils.PageUtil.toPageResult;
import cn.com.infinova.govern.common.model.exception.BusinessException;
import cn.com.infinova.govern.common.model.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Optional;
/**
 * *{tableInfo.tableComment}*业务处理接口实现类
 * @since  *{tool.currentDateTime()}*
 * @version 1.0.0
 * @author *{tool.author()}*
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class *{simpleClassName}* implements *{serviceImplSimpleName}* {
    private final *{daoSimpleName}* *{firstLowerDaoSimpleName}*;
</prefix>
<function name="add">
    /**
     * 添加*{tableInfo.tableComment}*
     *
     * @param *{firstLowerReqVoSimpleName}* *{tableInfo.tableComment}*POJO
     * @return 返回是否成功
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public *{respVoSimpleName}* add*{doSimpleName}*(*{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*) {
        *{firstLowerReqVoSimpleName}*.setId(null);
        Objects.requireNonNull(*{firstLowerReqVoSimpleName}*);
        *{doSimpleName}* *{firstLowerDoSimpleName}*=BeanUtil.copyProperties(*{firstLowerReqVoSimpleName}*,*{doSimpleName}*.class);
        if(*{firstLowerDaoSimpleName}*.insert(*{firstLowerDoSimpleName}*)!=1){
           throw new BusinessException(INTERNAL_SERVER_ERROR);
        }
        return BeanUtil.copyProperties(*{firstLowerDoSimpleName}*,*{respVoSimpleName}*.class);
    }
</function>
<function name="delete">
    /**
     * 删除*{tableInfo.tableComment}* 根据其id数组
     *
     * @param ids *{tableInfo.tableComment}*id数组
     * @return 返回是否成功
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean delete*{doSimpleName}*(Set&lt;Long&gt; ids<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>) {
        for(Long id:ids){
            get*{doSimpleName}*(id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,userId</if></foreach>).orElseThrow(()-> new BusinessException(REQUEST_DATA_QUERY_ERROR,id));
        }
        return CollUtil.isNotEmpty(ids) &amp;&amp; *{firstLowerDaoSimpleName}*.deleteBatchIds(ids) == ids.size();
    }
</function>
<function name="edit">
    /**
     * 编辑*{tableInfo.tableComment}*
     *
     * @param *{firstLowerReqVoSimpleName}* *{tableInfo.tableComment}*POJO
     * @return 返回是否成功
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public *{depend[4].simpleClassName}* edit*{doSimpleName}*(*{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*) {
        Objects.requireNonNull(*{firstLowerReqVoSimpleName}*);
        Long id = *{firstLowerReqVoSimpleName}*.getId();
        get*{doSimpleName}*(id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,*{firstLowerReqVoSimpleName}*.getUserId()</if></foreach>).orElseThrow(()-> new BusinessException(REQUEST_DATA_QUERY_ERROR,id));
        *{doSimpleName}* *{firstLowerDoSimpleName}*=BeanUtil.copyProperties(*{firstLowerReqVoSimpleName}*,*{doSimpleName}*.class);
        if(*{firstLowerDaoSimpleName}*.updateById(*{firstLowerDoSimpleName}*)!=1){
            throw new BusinessException(INTERNAL_SERVER_ERROR);
        }
        return BeanUtil.copyProperties(*{firstLowerDoSimpleName}*,*{respVoSimpleName}*.class);
    }
</function>
<function name="get">
    /**
     * 根据id获取*{tableInfo.tableComment}*信息
     *
     * @param id *{tableInfo.tableComment}*id
     * @return *{tableInfo.tableComment}*POJO
     */
    @Override
    public Optional&lt;*{respVoSimpleName}*&gt; get*{doSimpleName}*(Long id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>) {
        LambdaQueryWrapper&lt;*{doSimpleName}*&gt; lambdaQueryWrapper=new LambdaQueryWrapper&lt;&gt;();
        lambdaQueryWrapper.eq(*{doSimpleName}*::getId,id);
        <foreach v-for="column in tableInfo.columnList">
        <if v-if="column.name=='user_id'">
        lambdaQueryWrapper.eq(*{doSimpleName}*::getUserId,userId);
        </if>
        </foreach>
        return Optional.ofNullable(*{firstLowerDaoSimpleName}*.selectOne(lambdaQueryWrapper))
                .map(s-&gt;BeanUtil.copyProperties(s,*{respVoSimpleName}*.class));
    }
</function>
<function name="getList">
    /**
     * 根据id获取*{tableInfo.tableComment}*信息列表
     *
     * @param id *{tableInfo.tableComment}*id
     * @return *{tableInfo.tableComment}*POJO
     */
    @Override
    public List&lt;*{respVoSimpleName}*&gt; get*{doSimpleName}*List(Long id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>) {
        LambdaQueryWrapper&lt;*{doSimpleName}*&gt; lambdaQueryWrapper=new LambdaQueryWrapper&lt;&gt;();
        lambdaQueryWrapper.eq(*{doSimpleName}*::getId,id);
        <foreach v-for="column in tableInfo.columnList">
        <if v-if="column.name=='user_id'">
        lambdaQueryWrapper.eq(*{doSimpleName}*::getUserId,userId);
        </if>
        </foreach>
        return BeanUtil.copyToList(*{firstLowerDaoSimpleName}*.selectList(lambdaQueryWrapper),*{respVoSimpleName}*.class);
    }
</function>
<function name="getPage">
    /**
     * 分页方法获取*{tableInfo.tableComment}*信息列表
     *
     * @param pageReqVo 分页请求参数
     * @return *{tableInfo.tableComment}*数据列表
     */
    @Override
    public &lt;T extends PageReqVo&gt; PageResult&lt;*{respVoSimpleName}*&gt; get*{doSimpleName}*Page(T pageReqVo) {
        LambdaQueryWrapper&lt;*{doSimpleName}*&gt; lambdaQueryWrapper=new LambdaQueryWrapper&lt;&gt;();
        if(StrUtil.isNotBlank(pageReqVo.getSearchKey())) {
            <foreach v-for="column in tableInfo.columnList">
            <if v-if="column.name not in ['id','create_time','create_by', 'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', 'updated_by'] &amp;&amp; column.javaType=='String'">
            lambdaQueryWrapper.likeRight(*{doSimpleName}*::get*{tool.firstUpper(*{column.domainPropertyName}*)}*, pageReqVo.getSearchKey());
            </if>
            </foreach>
        }
        <foreach v-for="column in tableInfo.columnList">
        <if v-if="column.name=='user_id'">
        if(pageReqVo instanceof UserPageReqVo){
            lambdaQueryWrapper.eq(*{doSimpleName}*::getUserId, ((UserPageReqVo) pageReqVo).getUserId());
        }
        </if>
        </foreach>
        pageReqVo.bindDefaultQuery(lambdaQueryWrapper);
        Page&lt;*{doSimpleName}*&gt; page=new Page&lt;&gt;(pageReqVo.getPageNo(),pageReqVo.getPageSize());
        IPage&lt;*{doSimpleName}*&gt; pageResult = *{firstLowerDaoSimpleName}*.selectPage(page, lambdaQueryWrapper);
        return toPageResult(pageResult,*{respVoSimpleName}*.class);
    }
</function>
<suffix>
}
</suffix>
</template>
```

#### dom不可控制方法有依赖模版（HaveDependTemplateDomNoHandleFunctionTemplate）推荐使用

顾名思义这个不可控制方法 但是可以在这里使用依赖模版的语句

#### 可控制方法模板(DefaultHandleFunctionTemplate)

##### 定义格式

```java
*{prefix}*
  模板前缀
*{/prefix}*
*{function}*
    方法  
     注意:方法名必须以$$包裹  例如  $getById$
*{/function}*    
    .
    .
    .
*{suffix}*
    模板后缀
*{/suffix}*    
```

#### 不可控制方法模板(DefaultNoHandleFunctionTemplate)

无格式

### 与easy-code 对比

| 功能       | easy-code | code-builder |
|----------|-----------|--------------|
| 动态修改生成代码 | X         | √            |
| 友好的界面    | X         | √            |
| 灵活的自定义代码 | X         | √            |

### 什么是动态修改生成代码呢？

1.比如如果一个类中你只想生成其中一个方法，或者在某个类中通过操作界面动态的添加代码。

   例如 选中Controller模板，如果Controller 模板已生成文件  选择在文件的末尾处生成 ，那么将在 Controller 模板已生成文件末尾添加一段代码。

2.以某个方法作为模板，然后通过操作界面修改方法，加到其他模板类中

  例如 以Controller模板 的getById 作为模板  填写字段 age,name ,因子为id,那么将在Controller 模板已生成文件末尾添加  getByAgeAndName方法的实现 （以getById模板为基础)

### 如何自定义解析语法呢？

继承 `AbstractTemplateLangResolver`  自定义其实现就可以了

### 如何自定义模板实现呢？

1.可以控制方法的动态生成

继承`DefaultHandleFunctionTemplate` 类 自定义 实现生成模板方法

2.不可以控制方法的模板

继承`DefaultNoHandleFunctionTemplate` 类 自定义 实现生成模板方法

### 如何使用自定义方法界面呢？

1.字段名 是用来替代` 代表因子`中的 值的 如果是多个默认以`And`连接 ，代码会 使用字段名`替换模板方法中所有的代表因子`。

### 语法语句支持

#### dom模型语法支持(推荐)

下面这些语句都支持嵌套使用。

##### if语句

```xml
<if v-if="'abcd'=='ab'">
    test2ab
</if>
```

##### foreach语句

```xml
<foreach v-for="column in tableInfo2.columnList">
    <choose>
    <when test="column.test=='ab'">
        ab
        <if v-if="column.test=='ab'">
        test2ab
        </if>
    </when>
    <when test="column.test=='ac'">
       ac
    </when>
    <otherwise>
        acelse
    </otherwise>
    </choose>
</foreach>
```

##### var 定义变量语句

请注意：这个语法一般是在可控制方法的模版上面使用 再\<prefix\> 节点之前声明

```xml
<var name="doName" value="*{depend[0].className}*"/>
```

当然也可以这样 直接定义一个常量

```xml
<var name="doName" value="我是常量"/>
```

这个相当于定义了一个变量 doName

可以在模版当中 通过\*{doName}\* 来使用这个变量

##### choose语句

```xml
<choose>
    <when test="test=='ab'">
        ab
        <if v-if="test=='ab'">
        test2ab
        </if>
    </when>
    <when test="test=='ac'">
       ac
    </when>
    <otherwise>
        acelse
    </otherwise>
    </choose>
```

#### 非dom语法支持（即将废弃）

如果要使用语法变量 必须要使用  `*{}*` 包裹 代码

##### if语句

column.size对应TableInfo 的 ColumnInfo的size的值,ColumnInfo还有其他的属性 详情请查看代码

例子

```java
    *{if v-if='!column.isNull'}*
    @NotNull(message = "*{column.comment}*不允许为空")
    *{/if}*
    *{if v-if='column.javaType==String'}*
    @Size(max=*{column.size}*,min=1,message = "*{column.comment}*最大长度为*{column.size}*,至少长度为1")
    *{/if}*
    *{if v-if='column.javaType==Integer'}*
    @Max(value=*{column.size}*,message = "*{column.comment}*最大值为*{column.size}*")
    *{/if}*
```

##### foreach 语句

 column.domainPropertyName 对应 TableInfo 的 ColumnInfo的domainPropertyName 的值

 foreach 还有一个属性 trim 值true false(默认false) 去除两边的逗号

```java
    *{foreach v-for="column in tableInfo.columnList"}*

    public *{column.javaType}* get*{tool.firstUpper(*{column.domainPropertyName}*)}*()
    {
        return *{column.domainPropertyName}*;
    }

    public void set*{tool.firstUpper(*{column.domainPropertyName}*)}*(*{column.javaType}* *{column.domainPropertyName}*)
    {
        this.*{column.domainPropertyName}* = *{column.domainPropertyName}*;
    }
    *{/foreach}*
```

#### 通用工具方法

默认实现是 `ToolTemplateResolver` 的 `Function`

1. `*{tool.firstUpper(params)}*`  首字母大写  \*{tool.firstUpper(ab)}\* 首字母大写 Ab
2. `*{tool.upper(params)}*`  首字母大写  \*{tool.upper(ab)}\* 大写 AB
3. `*{tool.firstLower(params)}*`  首字母大写  \*{tool.firstLower(Ab)}\* 首字母大写 ab
4. `*{tool.lower(params)}*`  首字母大写  \*{tool.lower(Ab)}\* 小写 ab
5. `*{tool.currentDateTime()}*` 当前系统日期时间
6. `*{tool.author()}*` 获取code.properties 中配置的作者
7. `*{tool.allSqlColumn()}*` 获取 当前数据表格的所有字段(用于拼接sql)
8. `*{tool.upLevelPath(params)}*` 获取 上一级路径 比如包名 com.zzd 的上一级就是 com,com/zzd 为 com
9. `*{tool.sub(str,startIndex,endIndex)}*` 支持三种

```
`*{tool.sub(1,abc)}*  截取index 从1开始到末尾的字符串 bc
`*{tool.sub(abc,2)}*  截取index 从开始到末尾倒数2的字符串 a
`*{tool.sub(abc,1,2)}* 截取startIndex 从开始1到2的字符串 这三种语法  b
```

10. `*{tool.rep(allStr,oldStr,newStr)}` 字符串替换

### 团队协作

#### 如何将模版分享给其他成员

分享基础模版

1.右键指定基础模版

![image-20240929175038686](https://s3.bmp.ovh/imgs/2024/09/29/004bde7241397648.png)

2.点击复制按钮

![image-20240929175121072](README.assets/image-20240929175121072.png)

分享组合模版

![image-20240929174807597](https://s3.bmp.ovh/imgs/2024/09/29/82b91189132d0d50.png)

#### 成员拿到了地址怎么导入到工具当中？

点击模版=>新建

导入模版是基础模版

导入组合模版是组合模版

![image-20240929175157332](README.assets/image-20240929175157332.png)

#### 怎么将基础模版导入到组合模版当中？

右键选中组合模版=>选择导入模版，填写基础模版的地址，就可以导入了。

![image-20240929175339332](https://s3.bmp.ovh/imgs/2024/09/29/e1519ba828e1bde4.png)

### 通用模版使用

#### DataEntity 数据库对象模版

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

#### Dao 模版

```xml
package *{packageName}*;
import *{depend[0].className}*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
/**
 * *{tableInfo.tableComment}*业务处理接口DB类
 * @since  *{tool.currentDateTime()}*
 * @version 1.0.0
 * @author *{tool.author()}*
 */
public interface *{simpleClassName}* extends BaseMapper<*{depend[0].simpleClassName}*>{

}
```

#### ReqVo 模版

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
 * *{tableInfo.tableComment}*ReqVo
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

#### RespVo

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

#### ServiceInterface 模版

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

#### ServiceInterfaceImpl 模版

```xml
<template>
<var name="doName" value="*{depend[0].className}*"/>
<var name="doSimpleName" value="*{depend[0].simpleClassName}*"/>
<var name="firstLowerDoSimpleName" value="*{tool.firstLower(*{doSimpleName}*)}*"/>
<var name="daoName" value="*{depend[1].className}*"/>
<var name="daoSimpleName" value="*{depend[1].simpleClassName}*"/>
<var name="firstLowerDaoSimpleName" value="*{tool.firstLower(*{daoSimpleName}*)}*"/>
<var name="reqVoName" value="*{depend[2].className}*"/>
<var name="reqVoSimpleName" value="*{depend[2].simpleClassName}*"/>
<var name="firstLowerReqVoSimpleName" value="*{tool.firstLower(*{reqVoSimpleName}*)}*"/>
<var name="serviceImplName" value="*{depend[3].className}*"/>
<var name="serviceImplSimpleName" value="*{depend[3].simpleClassName}*"/>
<var name="respVoName" value="*{depend[4].className}*"/>
<var name="respVoSimpleName" value="*{depend[4].simpleClassName}*"/>
<prefix>
package *{packageName}*;

import *{doName}*;
import *{daoName}*;
import *{reqVoName}*;
import *{serviceImplName}*;
import *{respVoName}*;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import cn.com.infinova.govern.common.model.utils.PageResult;
import cn.com.infinova.govern.common.model.vo.PageReqVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Optional;
/**
 * *{tableInfo.tableComment}*业务处理接口实现类
 * @since  *{tool.currentDateTime()}*
 * @version 1.0.0
 * @author *{tool.author()}*
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class *{simpleClassName}* implements *{serviceImplSimpleName}* {
    private final *{daoSimpleName}* *{firstLowerDaoSimpleName}*;
</prefix>
<function name="add">
    /**
     * 添加*{tableInfo.tableComment}*
     *
     * @param *{firstLowerReqVoSimpleName}* *{tableInfo.tableComment}*POJO
     * @return 返回是否成功
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public *{respVoSimpleName}* add*{doSimpleName}*(*{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*) {
        *{firstLowerReqVoSimpleName}*.setId(null);
        Objects.requireNonNull(*{firstLowerReqVoSimpleName}*);
        *{doSimpleName}* *{firstLowerDoSimpleName}*=BeanUtil.copyProperties(*{firstLowerReqVoSimpleName}*,*{doSimpleName}*.class);
        if(*{firstLowerDaoSimpleName}*.insert(*{firstLowerDoSimpleName}*)!=1){
           throw new BusinessException(INTERNAL_SERVER_ERROR);
        }
        return BeanUtil.copyProperties(*{firstLowerDoSimpleName}*,*{respVoSimpleName}*.class);
    }
</function>
<function name="delete">
    /**
     * 删除*{tableInfo.tableComment}* 根据其id数组
     *
     * @param ids *{tableInfo.tableComment}*id数组
     * @return 返回是否成功
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean delete*{doSimpleName}*(Set&lt;Long&gt; ids<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>) {
        for(Long id:ids){
            get*{doSimpleName}*(id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,userId</if></foreach>).orElseThrow(()-> new BusinessException(REQUEST_DATA_QUERY_ERROR,id));
        }
        return CollUtil.isNotEmpty(ids) &amp;&amp; *{firstLowerDaoSimpleName}*.deleteBatchIds(ids) == ids.size();
    }
</function>
<function name="edit">
    /**
     * 编辑*{tableInfo.tableComment}*
     *
     * @param *{firstLowerReqVoSimpleName}* *{tableInfo.tableComment}*POJO
     * @return 返回是否成功
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public *{depend[4].simpleClassName}* edit*{doSimpleName}*(*{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*) {
        Objects.requireNonNull(*{firstLowerReqVoSimpleName}*);
        Long id = *{firstLowerReqVoSimpleName}*.getId();
        get*{doSimpleName}*(id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,*{firstLowerReqVoSimpleName}*.getUserId()</if></foreach>).orElseThrow(()-> new BusinessException(REQUEST_DATA_QUERY_ERROR,id));
        *{doSimpleName}* *{firstLowerDoSimpleName}*=BeanUtil.copyProperties(*{firstLowerReqVoSimpleName}*,*{doSimpleName}*.class);
        if(*{firstLowerDaoSimpleName}*.updateById(*{firstLowerDoSimpleName}*)!=1){
            throw new BusinessException(INTERNAL_SERVER_ERROR);
        }
        return BeanUtil.copyProperties(*{firstLowerDoSimpleName}*,*{respVoSimpleName}*.class);
    }
</function>
<function name="get">
    /**
     * 根据id获取*{tableInfo.tableComment}*信息
     *
     * @param id *{tableInfo.tableComment}*id
     * @return *{tableInfo.tableComment}*POJO
     */
    @Override
    public Optional&lt;*{respVoSimpleName}*&gt; get*{doSimpleName}*(Long id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>) {
        LambdaQueryWrapper&lt;*{doSimpleName}*&gt; lambdaQueryWrapper=new LambdaQueryWrapper&lt;&gt;();
        lambdaQueryWrapper.eq(*{doSimpleName}*::getId,id);
        <foreach v-for="column in tableInfo.columnList">
        <if v-if="column.name=='user_id'">
        lambdaQueryWrapper.eq(*{doSimpleName}*::getUserId,userId);
        </if>
        </foreach>
        return Optional.ofNullable(*{firstLowerDaoSimpleName}*.selectOne(lambdaQueryWrapper))
                .map(s-&gt;BeanUtil.copyProperties(s,*{respVoSimpleName}*.class));
    }
</function>
<function name="getList">
    /**
     * 根据id获取*{tableInfo.tableComment}*信息列表
     *
     * @param id *{tableInfo.tableComment}*id
     * @return *{tableInfo.tableComment}*POJO
     */
    @Override
    public List&lt;*{respVoSimpleName}*&gt; get*{doSimpleName}*List(Long id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>) {
        LambdaQueryWrapper&lt;*{doSimpleName}*&gt; lambdaQueryWrapper=new LambdaQueryWrapper&lt;&gt;();
        lambdaQueryWrapper.eq(*{doSimpleName}*::getId,id);
        <foreach v-for="column in tableInfo.columnList">
        <if v-if="column.name=='user_id'">
        lambdaQueryWrapper.eq(*{doSimpleName}*::getUserId,userId);
        </if>
        </foreach>
        return BeanUtil.copyToList(*{firstLowerDaoSimpleName}*.selectList(lambdaQueryWrapper),*{respVoSimpleName}*.class);
    }
</function>
<function name="getPage">
    /**
     * 分页方法获取*{tableInfo.tableComment}*信息列表
     *
     * @param pageReqVo 分页请求参数
     * @return *{tableInfo.tableComment}*数据列表
     */
    @Override
    public &lt;T extends PageReqVo&gt; PageResult&lt;*{respVoSimpleName}*&gt; get*{doSimpleName}*Page(T pageReqVo) {
        LambdaQueryWrapper&lt;*{doSimpleName}*&gt; lambdaQueryWrapper=new LambdaQueryWrapper&lt;&gt;();
        if(StrUtil.isNotBlank(pageReqVo.getSearchKey())) {
            <foreach v-for="column in tableInfo.columnList">
            <if v-if="column.name not in ['id','create_time','create_by', 'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', 'updated_by'] &amp;&amp; column.javaType=='String'">
            lambdaQueryWrapper.likeRight(*{doSimpleName}*::get*{tool.firstUpper(*{column.domainPropertyName}*)}*, pageReqVo.getSearchKey());
            </if>
            </foreach>
        }
        <foreach v-for="column in tableInfo.columnList">
        <if v-if="column.name=='user_id'">
        if(pageReqVo instanceof UserPageReqVo){
            lambdaQueryWrapper.eq(*{doSimpleName}*::getUserId, ((UserPageReqVo) pageReqVo).getUserId());
        }
        </if>
        </foreach>
        pageReqVo.bindDefaultQuery(lambdaQueryWrapper);
        Page&lt;*{doSimpleName}*&gt; page=new Page&lt;&gt;(pageReqVo.getPageNo(),pageReqVo.getPageSize());
        IPage&lt;*{doSimpleName}*&gt; pageResult = *{firstLowerDaoSimpleName}*.selectPage(page, lambdaQueryWrapper);
        return toPageResult(pageResult,*{respVoSimpleName}*.class);
    }
</function>
<suffix>
}
</suffix>
</template>
```

#### Controller 模版

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
 * @since  *{tool.currentDateTime()}*
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
     * @param *{firstLowerReqVoSimpleName}*       *{tableInfo.tableComment}*信息
     * @return 数据处理结果
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加*{tableInfo.tableComment}*", hidden=true)
    public Result&lt;*{respVoSimpleName}*&gt; add*{tableInfo.domainName}*(@Valid @RequestBody *{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*) {
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
    @ApiImplicitParam(paramType = "query", name = "idS", value = "*{tableInfo.tableComment}*id数组", required = true, dataType = "String")
    public Result&lt;Boolean&gt; delete*{tableInfo.domainName}*(@RequestParam Set&lt;Long&gt; ids) {
        return Result.success(*{firstLowerDoSimpleName}*.delete*{tableInfo.domainName}*(ids<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,UserUtils.getUserId()</if></foreach>));
    }
    </function>
    <function name="edit">
    /**
     * 修改*{tableInfo.tableComment}*
     *
     * @param *{firstLowerReqVoSimpleName}*       *{tableInfo.tableComment}*信息
     * @return 数据处理结果
     */
    @PutMapping("/edit")
    @ApiOperation(value = "修改*{tableInfo.tableComment}*", hidden=true)
    public Result&lt;*{respVoSimpleName}*&gt; edit*{tableInfo.domainName}*(@Valid @RequestBody *{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*) {
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
    @ApiOperation(value = "根据id获取*{tableInfo.tableComment}*", notes = "返回*{tableInfo.tableComment}*详细信息", hidden=true)
    @ApiImplicitParam(paramType = "query", name = "id", value = "*{tableInfo.tableComment}*id", required = true, dataType = "Long")
    public Result&lt;*{respVoSimpleName}*&gt; get*{tableInfo.domainName}*(@RequestParam Long id) {
        return Result.success(*{firstLowerDoSimpleName}*.get*{tableInfo.domainName}*(id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,UserUtils.getUserId()</if></foreach>).orElse(null));
    }
    </function>
    <function name="getAll">
    /**
     * 分页方法
     * 获取*{tableInfo.tableComment}*列表信息
     *
     * @param pageReqVo  分页参数
     * @return 数据处理结果
     */
    @GetMapping("/getAllList")
    @ApiOperation(value = "获取*{tableInfo.tableComment}*列表", notes = "返回*{tableInfo.tableComment}*列表信息", hidden=true)
    public Result&lt;PageResult&lt;*{respVoSimpleName}*&gt;&gt; getAll*{tableInfo.domainName}*Page(@Valid PageReqVo pageReqVo){
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

### 加群

如有疑问请加群 `948896114`

<img src="https://s3.bmp.ovh/imgs/2022/09/29/807bd5a01532920b.png" alt="image-20220929222640211" style="zoom: 33%;" />