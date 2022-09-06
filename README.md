##### 前言

代码生成器.

###### 亮点 

1.根据数据表格或自定义变量,和自定义的模板，生成任意你想生成的代码，避免项目中重复写相似的，大大提高开发效率。

2.动态操作模板中的每一个方法，生成你想生成目标文件的任意方法。

3.以已有模板中的方法为基础，根据自定义参数，生成类似的代码。

##### QUICK START

###### 1.通过代码直接启动

启动com.fpp.Main main方法

###### 2.下载 [codebuilder压缩包](https://github.com/bigbird-0101/code-builder/releases) 

解压点击启动**codebuilder.exe**

![image](https://github.com/bigbird-0101/code-builder/blob/master/images/projectfile.png)

**可修改data中的模板(模板必须为.template 后缀名) 达到修改生成的内容**

项目启动后
![image](https://github.com/bigbird-0101/code-builder/blob/master/images/projectfile2.png)

###### **参数详解**

1.`code.properties 配置文件详解`

1. `code.datasource.url`  数据源地址
2. `code.datasource.username` 数据源用户名
3. `code.datasource.password` 数据源密码
4. `code.project.file.project-author`  项目的作者

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

###### 模板详解

1. 可控制方法模板(DefaultHandleFunctionTemplate)

   定义格式

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

2. 不可控制方法模板(DefaultNoHandleFunctionTemplate)

   无格式

与easy-code 对比

| 功能             | easy-code | spring-code |
| ---------------- | --------- | ----------- |
| 动态修改生成代码 | X         | √           |
| 友好的界面       | X         | √           |
| 灵活的自定义代码 | X         | √           |

##### 什么是动态修改生成代码呢？

1.比如如果一个类中你只想生成其中一个方法，或者在某个类中通过操作界面动态的添加代码。

   例如 选中Controller模板，如果Controller 模板已生成文件  选择在文件的末尾处生成 ，那么将在 Controller 模板已生成文件末尾添加一段代码。

2.以某个方法作为模板，然后通过操作界面修改方法，加到其他模板类中

  例如 以Controller模板 的getById 作为模板  填写字段 age,name ,因子为id,那么将在Controller 模板已生成文件末尾添加  getByAgeAndName方法的实现 （以getById模板为基础)

##### 如何自定义解析语法呢？

继承 `AbstractTemplateLangResolver`  自定义其实现就可以了

##### 如何自定义模板实现呢？

1.可以控制方法的动态生成

继承`DefaultHandleFunctionTemplate` 类 自定义 实现生成模板方法

2.不可以控制方法的模板

继承`DefaultNoHandleFunctionTemplate` 类 自定义 实现生成模板方法

##### 如何使用自定义方法界面呢？

1.字段名 是用来替代` 代表因子`中的 值的 如果是多个默认以`And`连接 ，代码会 使用字段名`替换模板方法中所有的代表因子`。

##### 解析语法

如果要使用语法 必须要使用  `*{}*` 包裹 代码

###### if语句

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

###### foreach 语句

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

######  工具方法

默认实现是 `ToolTemplateResolver` 的 `Function`

1. `*{tool.firstUpper(params)}*`  首字母大写  \*{tool.firstUpper(ab)}\* 首字母大写 Ab
2. `*{tool.upper(params)}*`  首字母大写  \*{tool.upper(ab)}\* 大写 AB
3. `*{tool.firstLower(params)}*`  首字母大写  \*{tool.firstLower(Ab)}\* 首字母大写 ab
4. `*{tool.lower(params)}*`  首字母大写  \*{tool.lower(Ab)}\* 小写 ab
5. `*{tool.currentDateTime()}*` 当前系统日期时间
6. `*{tool.author()}*` 获取code.properties 中配置的作者
7. `*{tool.allSqlColumn()}*` 获取 当前数据表格的所有字段(用于拼接sql)
8. `*{tool.upLevelPath(params)}*` 获取 上一级路径 比如包名 com.zzd 的上一级就是 com,com/zzd 为 com

如有疑问请加群 `948896114`

![image](https://github.com/bigbird-0101/code-builder/blob/master/images/quncode.png)
