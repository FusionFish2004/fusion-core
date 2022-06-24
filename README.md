# FusionCore

FusionCore是本人为简化bukkit插件开发过程从而开发的一款前置插件。  
本前置插件中集成了Reflections以及fastJSON库，以便开发。

***
## 开始
在您的插件主类中继承FusionPlugin类，并实现enable和disable方法：
````
public class YourPlugin extends FusionPlugin {
    //继承FusionPlugin父类
    
    @Override
    protected final void enable() {
        //插件启动时触发本方法
    }
    
    @Override
    protected final void disable() {
        //插件关闭时触发本方法
    }
}
````
并且在`plugin.yml`中添加前置插件：
````
depend
 - FusionCore
````
随后您就可以使用本前置插件提供的许多功能。
***
## 功能
### 指令自动注册与智能解析
插件启动时会自动扫描包中的所有标注好的指令类并自动注册，省去繁琐的步骤。  
命令执行时将自动解析到类中的方法，参数也将一同自动传入。
#### 简单指令
形如`/simplecommand`的指令便为简单指令
新建一个类并继承`BukkitSimpleCommand`，并且在类上标注`@BukkitCommand`注解：
````
@BukkitCommand
public class YourSimpleCommand extends BukkitSimpleCommand {
    //注意这里必须为无参构造器，不然反射注册时会报错
    protected YourSimpleCommand() {
        //设置指令名
        super("simplecommand");
        //设置指令别名
        setAliases(List.of("alias1", "alias2","alias3"));
        //设置指令描述
        setDescription("...");
        //设置指令权限
        setPermission("permission.yourpermission");
        //设置指令提示
        setUsage("/simplecommand");
    }
}
````
创建一个方法并且标注`@Invoke`注解，执行时会自动传入一个CommandSender参数：
````
    @Invoke
    public void invoke(CommandSender sender) {
        //执行的代码...
    }
````
#### 复合指令
形如`/compositecommand sub1 sub2 arg1 arg2`的指令便为复合指令 
新建一个类并继承`BukkitCompositeCommand`，并且在类上标注`@BukkitCommand`注解：
````
@BukkitCommand
public class YourCompositeCommand extends BukkitCompositeCommand {
    //注意这里必须为无参构造器，不然反射注册时会报错
    public YourCompositeCommand() {
        //设置主指令名
        super("compositecommand");
        //设置指令别名
        setAliases(List.of("alias1", "alias2","alias3"));
    }
}
````
创建一个方法并标注`@SubCommand`注解，执行时便可将所有参数解析后传入。
|参数|类型|描述|
|:-:|:-:|:-------------|
|command|String|必填，指定子命令，如主指令为`compositecommand`，该参数设置为`sub1.sub2.sub3`，在执行指令`/compositecommand sub1 sub2 sub3 args..`时会自动解析到本方法|
|playerCommand|boolean|默认值为false，指定是否只能由玩家执行该指令|
|adminCommand|boolean|默认值为false，指定是否只能由管理员执行该指令|
|usage|String|默认为空，指定命令提示|
|permission|String|默认为空，指定本命令是否需要特定权限|
````
    @SubCommand(
            command = "sub1.sub2",
            playerCommand = true,
            adminCommand = true,
            usage = "/compositecommand sub1 sub2 [String]",
            permission = "compositecommand.permission"
    )
    public void invoke(CommandSender sender, String string) {
        //指令执行时将会自动将所需参数传入
        //当解析错误时会报ParseException
    }
````
可以解析的参数类型：
|参数类型|描述|
|:--:|:-------------|
|Boolean/boolean|可解析`true`或`false`（不分大小写）|
|Date|可解析形如`yyyy/MM/dd`或`yyyy-MM-dd`等的字符串|
|Double/double|解析为双精度浮点数|
|Float/float|解析为单精度浮点数|
|Integer/int|解析为整形|
|Long/long|解析为长整型|
|String|解析为字符串，注意`null`也将解析为空|
|UUID|解析为UUID|
|Vector|解析为向量，需要输入形如`(x,y,z)`的字符串|
|World|解析为世界，需要输入世界名|   

**如果指令输入的参数为`null`将会解析为空**
### HTTP服务
新建一个类并实现`Handler`接口，并在类上和方法上标注`@RequestHandler`注解。  
类注解中的参数`path`指定您要创建http服务的父路径，而方法中的路径指定http服务的子路径，例如父路径为
`/api/super`，子路径为`/sub/sub`，则会创建一个路径为`/api/super/sub/sub`的http服务。
````
@RequestHandler(path = "/yourpath")
public class YourHandler implements Handler {
    
    @RequestHandler(path = "/yourpath)
    public String invoke() {
        
    }
}
````
注意方法的返回值必须为String，不然会报错。  
方法要解析的每个参数中必须标注`@RequestParam`注解，其中`paramName`指定了http请求中传入的参数名，`defaultValue`则指定了默认参数值，默认为`null`。   
注解完的代码在执行时便可自动解析参数并传入方法。
````
    @RequestHandler(path = "/yourpath")
    public String invoke(
        @RequestParam(paramName = "double1", defaultValue = "0") Double d1,
        @RequestParam(paramName = "string1") String s1
    ) {
        //...
    }
````
插件启动时会自动注册所有http服务，可在配置文件中配置http服务器的端口：
````
web:
  http-service: false
  http-port: 11451
````
