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
形如`/simplecommand`的指令便为简单指令（执行时无参数）  
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
形如`/compositecommand sub1 sub2 arg1 arg2`的指令便为复合指令（执行时有参数）  
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
````


````

