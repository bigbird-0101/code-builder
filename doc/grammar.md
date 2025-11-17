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
@Size(max =*{column.size}*,min=1,message ="*{column.comment}*最大长度为*{column.size}*,至少长度为1")
        *{/if}*
        *{if v-if='column.javaType==Integer'}*
@Max(value =*{column.size}*,message ="*{column.comment}*最大值为*{column.size}*")
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