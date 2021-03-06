Webit Script -- to be awesome
====

A template-like script and engine, all writen with Java **(Java 5+)**.

It's grammar is very like Javascript, and with `<% %>` `${ }` like in JSP

**Download: [Jar][jar]    [Javadoc][doc]    [Sources][sources]**

**QQ 群(Group): 302505483**

## How to use(如何使用)

### Maven

    <repositories>
        <repository>
            <id>webit-script</id>
            <name>Webit script</name>
            <url>http://zqq90.github.io/maven/</url>
        </repository>
    </repositories>
    <dependencies>
        ...
        <dependency>
            <groupId>com.github.zqq90.webit-script</groupId>
            <artifactId>webit-script</artifactId>
            <version>1.2.1</version>
        </dependency>
        ...
    </dependencies>

### Or Add jars

 + `webit-script-1.2.1.jar`

### Code in Java like this

    // !! engine 并不会被缓存, 请根据需要自行实现 Engine的单例模式
    Engine engine = Engine.createEngine("/webit-script-config.props", extraSettingsMap);
    ...
    // template 已缓存, 线程安全, 并自动检测模板源是否被更新
    // 当然您也可以缓存 Template 实例，模板更新时更新实例内部AST, 其实例不会变化
    Template template = engine.getTemplate("/your/template/path/filename.ext");
    ...
    template.merge(parametersMap, outputStream); 
    //template.merge(parametersMap, writer);

## Config(配置)

+ 配置文件格式: Use Jodd-props, see:[Jodd Props doc][url_props_doc]
  `Tips: Java-Properties also works`
+ 多文件支持 "/webit-script-config1.props,/webit-script-config2.props"
+ 可选额外参数: extraSettingsMap 类型为Map, 支持props 宏
+ 默认配置: [webit-script-default.props] [default_config]

## Grammar(语法)

### Hello word

    Hello Webit Script!
    <%
    var books;
    {
        for(book : books){
    %>
    ${for.iter.index}.《${book.name}》 ￥${book.price}
    <%
        }
    }
    {
        //this is a function
        var func = function(a, b){
            return a + b + arguments[3];
        };
        echo func("a", "b", "c");
        echo '\n';
    }
    {
        var map = {
            1: 1,
            "key2": "value2",
            3: 2 + 1
        };
        map[5] = 2 + 3;
        for(key, value : map){
            echo key + ":" +value + "\n";
        }
    }
    %>

更多实例可见:[测试模板][tests]

### 结构

+ 脚本必须都放在`<% %>` 内. `<% .code inside.. %> plain text outside`
+ 替换占位符`${}` 只能允许带返回值的单个表达式,只能在脚本块`<% %>` 以外使用.`${ .. expression .. }` 
+ 转义字符 `\\` to `\` , `\<%` to `<%`, `\${` to `${`
+ Node: 只有紧挨 `<%` `${` 的 `\` 才需要转义 , 
+ 转义窍门: 偶数个 `\` 会 打印 一半数量的`\` 并进入代码段, 
            奇数 会 打印 (count-1)/2 个 `\` 然后打印被转移的符号。
            

### 注释

+ 单行注释 `//` 
+ 块注释 `/* */` 


### 关键字

    var  super  this
    if  else
    switch  case  default
    for  do  while  break  continue 
    function  return
    import  include  echo
    native  new  @import

### 保留的关键字

    static  instanceof  class  const  final
    throw  try  catch  finally

### 操作符
*与Java 保持一致，顺序按优先级从高到低*

    [] . () @
    =>
    !  ~  ++  --  – (取负)
    *  /  %
    +  -
    <<  >>  >>>
    <  <=  >  >=
    ^
    |
    &&
    ||
    ?:
    ..
    =  +=  -=  *=  /=  %=  ^=  <<=  >>=  >>>=

### 语句
+ 结尾分号不能省略

### 作用域(代码段) `{ }`
+ 作用域引用上层变量
+ 本层作用域变量不会影响上层
+ 同一作用域不能重复声明变量
+ **模板传入的变量仅在该作用域查找同名变量并赋值**
  *1. 调用模板传入的变量; 2.import 返回的变量*

### 变量
#### 变量声明 var

    var a;
    var a, b, c=0, d="d";

#### 变量名规则
+ 先声明 后使用，所有变量 必须全部声明
+ 可开启 弱声明模式，所有变量不需要 事先声明，解析时自动声明
+ 对大小写敏感
+ 不能使用关键字
+ 仅能包含 0-9a-zA-Z_$
+ 特殊变量名: 
++ `super.` 用于 取得指定上层且仅该层作用域的变量, 可嵌套`super.super.a`
++ `this.` 用于 取得本层且仅本层作用域的变量, 可嵌套`this.super.a`
++ `for.iter` 用于 最近一层for循环的 迭代状态对象, 可使用`super``this` 限定作用域`super.for.iter`

### 数据结构
#### 拥有动态类

    var x                             //  null
    var x2 = 6;                    //  数字
    var x3 = "Bill";               //  字符串
    var x4 = 'a';                   //  字符
    var x5 = [1, "string"];     //  数组
    var x6 = {};                    // Map

#### 字符串
+ 转义，`\\` `\"` `\'` `\n` `\r` `\t` `\f` `\b`
+ 允许换行，行后转义字符 可屏蔽该换行符

~~~~~
var string = "第一行  \
这还是在第一行
这是第二行\n第三行\n
这是第五行，第四行是空行";
~~~~~

### 数字

    var x1=34;  //Integer
    var x2=34L;  //Long
    var x3=34.00; //Double
    var x4=34.00D;  //Double
    var x5=34.00F;  //Float
    var x6 = 0b10101;  //二进制
    var x7 = 0123; //八进制
    var x8 = 0x1A; //十六进制

### 布尔

    var x = true;
    var y = false;


### 数组

#### 带初始值的数组

    var array = [1, "a string", book];
    var item;
    item = array[0];
    item = array[1];
    item = array[2];
    array[0] = "a new value"；

#### Native 方法声明定长数组,

    // 引入生成数组的 native 方法
    var new_int_array = native int [];
    var new_Object_array = native Object [];
    var new_DateTime_array = native java.util.DateTime [];
    
    //得到定长数组
    var int_array = new_int_array(5); //长度为5 的int数组
    var objects = new_Object_array(5);//长度为5 的Object数组
    
    var a;
    a = objects[4];
    objects[4]=4; // 自动 装箱为Integer 然后放入数组
    var len = objects.length; //数组长度
    len = objects.size; //数组长度
    
    //不定长数组 可使用Java提供的List实现
    var new_list = native new java.util.ArrayList();
    var list_add = native java.util.List.add(Object);
    
    var list = new_list();
    list@list_add(0); 
    list@list_add(1);
    
    var a = list[0];
    list[0] = "zero";
    list[1] = "a string";

### Map

    var map = {};
    var map2 = {1:1,"2","2"};
    map["key"] = "a string value";
    
    var value = map[1];
    value = map["2"];
    value = map["key"];

### Java对象
#### 声明

    var new_list = native new java.util.ArrayList();
    var list = new_list();
    var list2 = new_list();

#### 访问属性

    var book;
    var name = book.name; // book.getName();
    book.name = "new name"; //book.setName("new name"); 

#### 访问方法
*访问方法必须事先native导入成本地函数*

    var list_add = native java.util.List.add(Object);
    list@list_add(0);
    list_add(list, 1);

*访问静态方法*

    var now = native java.lang.System.currentTimeMillis();
    echo now();
 
### 函数

#### 声明
+ 格式同java
+ 可变参数, 
+ 可通过 arguments 获得所有传入的参数, java类型为 Object[]
+ 可访问父层作用域
+ 函数内部可嵌套函数


~~~~~
var outSideVar;
var a;
var myFunc = function(arg1, arg2){
    var arg3 = arguments[3]; // 如果没有将抛出异常,最好通过 arguments.size确认
    outSideVar = "a new "; //可访问
    var a = 0; //内部变量
    super.a ++ ; //访问上层变量
    var func = function(){ ... }; //内部嵌套函数
}; //不要忘了分号！！！
~~~~~


#### 导入Java内的 方法
+ 仅可导入公共类的公共方法, 包括静态方法 和 成员方法
+ 可使用`@import` 导入类名 或者包名 用法同Java里的 `import`, 以简化类名输入
+ ~~@import  java.util.*;~~ v1.2.0+ 不再支持导入包


~~~~~
@import  java.lang.System; //实际上默认已经导入  java.lang.* 只是演示使用方法
@import  java.util.List;
@import  java.util.ArrayList;
var now = native java.lang.System.currentTimeMillis();
var list_add = native List.add(Object);
var new_list = native new ArrayList(); // 导入 构造函数
var new_list2 = native new ArrayList(int); // 导入 构造函数
~~~~~


### 调用
+ 可变参数个数, 多余函数同样会被传入, 多余函数是否被使用 取决于函数本身
+ 缺少的参数 自动 null 填充, *为了良好的设计 不建议使用缺少函数自动填充*
+ 可使用@ 将第一个参数 外置


~~~~~
func(arg1, arg2);
//等同于
arg1@func(arg2);
list_add(list, item);
//等同于
list@list_add(item);
~~~~~


### 重定向输出符 `=>`
+ 作用: 将指定 范围 产生的输出流 重定向到 指定变量
+ 意义: 可以延后输出
+ **使用对象: 1. 代码段；  2. 函数调用**
+ 数据格式: 使用OutputStream 时, 为 byte[] ; 使用 Writer 时, 为String.


~~~~~
    var out;
    var book;
    //代码段 输出重定向
    {
    echo "a String";
    >${book.name} <
    } => out; //不要忘了分号！！！
    // "a String" 以及 book.name 都将输出到 out
    
    var out;
    // 函数 输出重定向
    func() => out;
    //由于 `=>` 具有较高的优先级，也可以这么些
    var a = arg1@func() => out +1; 
    //此时 a为 func()+1 , func() 本次执行的输出内容赋值给out
~~~~~


### import & include
+ 区别: import  将把调用模板的 上层变量 推入调用层的当前已存在变量
+ 共同点: 都会在调用位置产生 输出
+ 将使用默认的Loader 加载模板，可使用相对路径或绝对路径
+ 可跟随 一个 map 格式的传参
+ 模板名可以动态生成
+ import 可支持指定需要导出的变量, 否则只导出本层作用域内的同名变量


~~~~~
//相对路径
include "./book-head.wtl";
//等同于 
include "book-head.wtl";
//绝对路径
include "/copyright.wtl";
//动态模板名
var style = "";
import "book-list-"+ style  +".wtl";
//可传入参数 函数同样也可以作为变量传输
var func = function(){}; 
var book;
import "book-head.wtl"  {"book": book, "func":func};
//传入Map 变量作为参数
var map =  {"book": book, "func":func}；
map["one"] = 1; 
import "book-head.wtl"  {map};
//导出指定变量
var a;
var b;
//导出 : a 到a ，c 到 b
import "book-head.wtl"  {"param1":1}  a,b=c;
~~~~~

### 关于条件判断的三种情况

+ 如果是boolean(Boolean)值 会原封返回
+ **如果 ==null 返回 false**
+ **如果 是空集合 或者 空数组 (.size==0)  返回 false **
+ 否则 返回 true


### 三元条件运算符 & 其简写
+ **操作符按 自右向左 结合 [不是执行顺序], 详解看下面例子**
+ **简写时 `?:` 之间不能有空白**


~~~~~
var a1 = isTrue ? "Yes" : "No";
//简写
var a2 = value ?: defaultValue; //取默认值
//自右向左 结合
var x =  expr1 ?  expr3 :  expr2 ? expr4 : expr5;
//这个等同于
var x =  expr1 ?  expr3 :  (expr2 ? expr4 : expr5);
// 如果 是 自左向右 就会变成这样
var x =  (expr1 ?  expr3 :  expr2) ? expr4 : expr5;
//简写 就按 从左向右 “执行” 
var a4 = list1 ?: list2 ?: list3;
~~~~~

### 判断语句
#### 判断表达式 ?:
#### 判断控制语句 if - else if - else
+ 不能省略 `{  }`


~~~~~
if( ... ){
    ...;
}else if(...){
    ...;
}else{
    ...;
}
~~~~~


### 循环控制语句
+ 支持 数组,  java.util.Collection, java.util.Iterator, java.util.Enumeration, CharSequence, java.util.Map, 整型递增/递减
+ 当集合为null 或者为空时将不会执行循环
+ 支持 else , 可选, 当不符合执行循环体的条件时执行else体.

#### for-in

    //集合 数组 等
    for(item : list){
        echo item;
        //echo for.iter.index; // .isFirst .hasNext .isOdd .isEven
    } else{
        echo "list is empty";
    }
    //递增 
    for(i: 3..6){
        echo i;
    }
    //递减
    for(i: 6..3){
        echo i;
        //支持 for.iter.*
    }


#### for-in Map version

    for(key, value : map){
        echo key + " = " value;
        echo "\n";
        //同样支持 for.iter.*
    }

#### while do-while 
+ 不支持 for.iter 特殊变量


~~~~~
//
var iter;
... ;
while(iter.hasNext){
    var item = iter.next;
    ....;
}
//
do{
    ....;
}while( ... );
~~~~~



### Switch-Case

+ 支持普通 Object, 包括 String 
+ 使用  Object.equls() 判断是否相等
+ 需要 break, 否则无条件继续执行下一个标签的句柄
+ 每个case 命名空间独立


~~~~~
switch(a){
    case 1:
        ....;
        break;
    case "c": //String
        ....;
        break;
    case 'c': //Char
        ....;
        break;
    default:
        ....;
}
~~~~~

### break continue
+ **支持 label, 直接操作该循环体 或 switch**


~~~~~
//break continue
outter: for(i: 6..3){
    echo i;
    //支持 for.iter.*
    inner: for(item : list){
        if( ..... ){
           break outter;
        }
        .....;
        break; // break inner;
    }
    //
    switch(a){
        ...;
        case 'x':
           break outter;
        ...;
    }
}
~~~~~


### 正在完善。。。

### 其他



## Performance(性能)

+ 缺省开启ASM构建Native 调用减少反射, 不同于将整个模板编译成Java字节码,该方法不会造成无限制的perm溢出;
+ 解析之后的Template AST会放入缓存, 检测到模板源文件改变时将重新加载资源并解析;
+ 性能测试结果比较理想, 待比较权威的模版测试程序;
+ 使用OutputStream 输出时, 选择 SimpleTextStatmentFactory 将会预先将纯文本根据缺省编码编码成字节流. 
+ boilit/ebm 测试结果 [![boilit/ebm](https://github.com/boilit/ebm)]  [![or see](http://boilit.github.io/bsl/zh/ability/jdk7utf8.html)]


~~~~~
Engine                                Time            Size
BSL-2.0.0                              559        68118050
webit-script-1.1.4                     590        68318250
HTTL-1.0.11                            958        68118050
BeeTL-1.25.01                          958        68138070
Rythm-1.0.0-b10-SNAPSHOT              1624        48728680
Velocity-1.7                          1834        75046912
FreeMarker-2.3.19                     2369        68157440
JdkStringBuffer-1.7.0_40               606        67395584
JdkStringBuilder-1.7.0_40              735        67395584
~~~~~


## SPI

+ TextStatmentFactory  对模板纯文本的存贮以及输出形式进行管理
+ Filter   输出过滤
+ CoderFactory   编码/解码
+ Loader   模板资源加载(原ResourceLoader)
+ Logger   日志
+ GetResolver, SetResolver, OutResolver  Bean属性解释器
+ NativeSecurityManager   Native调用安全管理器


## Requirements(依赖)

+ null

## Contributing(贡献)

+ Translation works
+ code & doc: fork -> pull requests
+ idea & bug report: [github-issue] [new_issue]
+ donate:


## License(许可证)
 
**Webit Script** is released under the BSD License. See the bundled LICENSE file for
details.
**Webit Script** 依据 BSD许可证发布。详细请看捆绑的 [LICENSE][license] 文件。

## Other License

+ **Jodd**  under the BSD License. [License file][jodd_license]
+ **ASM-3.3.1**  under the BSD License.[License file][asm_license]



[jar]: http://zqq90.github.io/maven/com/github/zqq90/webit-script/webit-script/1.2.1/webit-script-1.2.1.jar
[jar_joddin]: http://zqq90.github.io/maven/com/github/zqq90/webit-script/webit-script-joddinside/1.2.1/webit-script-joddinside-1.2.1.jar
[sources]: http://zqq90.github.io/maven/com/github/zqq90/webit-script/webit-script/1.2.1/webit-script-1.2.1-sources.jar
[doc]: http://zqq90.github.io/maven/com/github/zqq90/webit-script/webit-script/1.2.1/webit-script-1.2.1-javadoc.jar
[url_props_doc]: http://jodd.org/doc/props.html
[tests]: https://github.com/zqq90/webit-script/tree/master/webit-script/src/test/resources/webit/script/test/tmpls
[default_config]: https://github.com/zqq90/webit-script/blob/master/webit-script/src/main/resources/webit-script-default.props
[new_issue]: https://github.com/zqq90/webit-script/issues/new
[license]: https://github.com/zqq90/webit-script/blob/master/LICENSE

[jodd_down]: http://jodd.org/download/index.html
[jodd_site]: http://jodd.org/index.html
[jodd_doc]: http://jodd.org/doc/index.html
[jodd_github]: https://github.com/oblac/jodd
[jodd_license]: http://jodd.org/license.html
[asm_license]: http://asm.ow2.org/license.html




[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/zqq90/webit-script/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

