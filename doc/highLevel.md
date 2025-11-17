### 什么是动态修改生成代码呢？

1.比如如果一个类中你只想生成其中一个方法，或者在某个类中通过操作界面动态的添加代码。

例如 选中Controller模板，如果Controller 模板已生成文件 选择在文件的末尾处生成 ，那么将在 Controller 模板已生成文件末尾添加一段代码。

2.以某个方法作为模板，然后通过操作界面修改方法，加到其他模板类中

例如 以Controller模板 的getById 作为模板 填写字段 age,name ,因子为id,那么将在Controller 模板已生成文件末尾添加
getByAgeAndName方法的实现 （以getById模板为基础)

### 如何自定义解析语法呢？

继承 `AbstractTemplateLangResolver`  自定义其实现就可以了

### 如何自定义模板实现呢？

1.可以控制方法的动态生成

继承`DefaultHandleFunctionTemplate` 类 自定义 实现生成模板方法

2.不可以控制方法的模板

继承`DefaultNoHandleFunctionTemplate` 类 自定义 实现生成模板方法

### 如何使用自定义方法界面呢？

1.字段名 是用来替代` 代表因子`中的 值的 如果是多个默认以`And`连接 ，代码会 使用字段名`替换模板方法中所有的代表因子`。