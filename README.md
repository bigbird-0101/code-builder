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

1. 选中组合模版
2. 右侧页面选中目标表名 （如果不需要表，则指定配置文件）
3. 选中指定模版
4. 点击导航栏的运行按钮，点击生成按钮

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

[dom可控制方法模版(DomHandleFunctionTemplate)](doc/templates/DomHandleFunctionTemplate.md)

#### dom不可控制方法模版(DomNoHandleFunctionTemplate) 推荐使用

##### 模版定义

```xml
<template>
    模版内容
</template>
```

##### 例子：

[dom不可控制方法模版(DomNoHandleFunctionTemplate)](doc/templates/DomNoHandleFunctionTemplate.md)

#### dom可控制方法有依赖模版（HaveDependTemplateDomHandleFunctionTemplate）推荐使用

这个模版定义与 DomHandleFunctionTemplate 相同 与 他的却别就是 可以在模版文件当中使用

\*{depend[1].simpleClassName}\* 这个语句 代表的意思是 第二个模版的简单类型，他这个索引是从0开始

##### 例子

[dom可控制方法有依赖模版(HaveDependTemplateDomHandleFunctionTemplate)](doc/templates/HaveDependTemplateDomHandleFunctionTemplate.md)

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

### [进阶](doc/highLevel.md)

### [语法语句支持](doc/grammar.md)

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

### [团队协作 分享和导入模版](doc/teamwork.md)

### 通用模版使用

#### [DataEntity 数据库对象模版](doc/templates/DataEntity.md)

#### [Dao 模版](doc/templates/Dao.md)

#### [ReqVo 模版](doc/templates/ReqVo.md)

#### [RespVo 模版](doc/templates/RespVo.md)

#### [ServiceInterface 模版](doc/templates/ServiceInterface.md)

#### [ServiceInterfaceImpl 模版](doc/templates/ServiceInterfaceImpl.md)

#### [Controller 模版](doc/templates/Controller.md)

### 加群

如有疑问请加群 `948896114`

<img src="https://s3.bmp.ovh/imgs/2022/09/29/807bd5a01532920b.png" alt="image-20220929222640211" style="zoom: 33%;" />