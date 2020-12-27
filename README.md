##### 前言

Spring项目代码生成器.

###### 亮点 

1.根据数据表格和自定义的模板，生成任意你想生成的代码，避免项目中重复写相似的，大大提高开发效率。

2.动态操作模板中的每一个方法，生成你想生成目标文件的任意方法。

3.以已有模板中的方法为基础，根据自定义参数，生成类似的代码。

##### QUICK START

###### 1.通过代码直接启动

启动StartMain main方法

###### 2.下载 spring-code包 

点击启动spring-code.bat

**参数详解**

1.`code.properties 配置文件详解`

1. `code.datasource.url`  数据源地址
2. `code.datasource.username` 数据源用户名
3. `code.datasource.password` 数据源密码
4. `code.project.file.project-complete-url` 项目的完整地址  项目地址+源码路径  
5. `code.project.file.project-target-packageurl`  源码的实际路径

`2.templates.json 配置文件详解`

配置的模板  为json数组

1. `fileName`  模板文件名(必须与data/template文件名一致)
2. `name` 模板的名字
3. `path` 最终生成代码的路径(注意该路径是基于项目+代码路径的)
4. `isHandleFunction`  是否是能够控制模板中的方法的模板 1-是 0-否
5. `filePrefixNameStrategyType` 文件的前缀命令策略   （默认）0-从表格名第四个字符开始+path的首字母大写  1-从表格名的第四个字符开始
6. `fileSuffixName` 文件的后缀名  默认 java

点击启动`spring-code.bat`

与easy-code 对比

| 功能             | easy-code | spring-code |
| ---------------- | --------- | ----------- |
| 动态修改生成代码 | X         | √           |
| 友好的界面       | X         | √           |
| 灵活的自定义代码 | X         | √           |

##### 什么是动态修改生成代码呢？

1.比如如果一个类中你只想生成其中一个方法，或者在某个类中通过操作界面动态的添加代码。

2.以某个方法作为模板，然后通过界面修改方法，加到其他模板类中

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

column.size对应

TableInfo 的 ColumnInfo的size的值,ColumnInfo还有其他的属性 详情请查看代码

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

column.domainPropertyName 对应
TableInfo 的 ColumnInfo的domainPropertyName 的值
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



如有疑问请加群 `948896114`

![image](https://github.com/bigbird-0101/spring-code/blob/master/quncode.png)
